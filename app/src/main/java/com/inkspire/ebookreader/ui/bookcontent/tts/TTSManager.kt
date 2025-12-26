package com.inkspire.ebookreader.ui.bookcontent.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.inkspire.ebookreader.domain.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

class TTSManager(
    context: Context
) : TextToSpeech.OnInitListener {
    private val tts: TextToSpeech = TextToSpeech(context, this)

    private val _events = MutableSharedFlow<TTSEvent>(
        extraBufferCapacity = 64
    )
    val events: SharedFlow<TTSEvent> = _events.asSharedFlow()
    private lateinit var bookInfo: Book
    private var chapterContent: List<String> = emptyList()

    private var paragraphOffset: Int = 0
    private var lastWordStartInSegment: Int = 0

    private var currentChunkOffsets = mutableListOf<Int>()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    if (utteranceId?.startsWith("para_") == true) {
                        val parts = utteranceId.split("_")
                        if (parts.size >= 6) {
                            val chunkIndex = parts[3].toInt()
                            val totalChunks = parts[5].toInt()

                            if (chunkIndex == totalChunks - 1) {
                                paragraphOffset = 0
                                lastWordStartInSegment = 0
                                checkPLayNextParagraph()
                            }
                        }
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {}

                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    super.onRangeStart(utteranceId, start, end, frame)

                    if (utteranceId != null && utteranceId.startsWith("para_")) {
                        try {
                            val parts = utteranceId.split("_")
                            val chunkIndex = parts[3].toInt()

                            val chunkStartOffset = currentChunkOffsets.getOrElse(chunkIndex) { 0 }
                            val globalStart = chunkStartOffset + start
                            val globalEnd = chunkStartOffset + end

                            lastWordStartInSegment = globalStart

                            _events.tryEmit(TTSEvent.OnRangeStart(globalStart, globalEnd))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
    }

    fun checkPlayNextChapter() {
        _events.tryEmit(TTSEvent.OnRangeStart(0, 0))
        _events.tryEmit(TTSEvent.CheckPlayNextChapter)
    }

    fun checkPlayPreviousChapter() {
        _events.tryEmit(TTSEvent.OnRangeStart(0, 0))
        _events.tryEmit(TTSEvent.CheckPlayPreviousChapter)
    }

    fun checkPLayNextParagraph() {
        _events.tryEmit(TTSEvent.OnRangeStart(0, 0))
        _events.tryEmit(TTSEvent.CheckPlayNextParagraph)
    }

    fun startReading(paragraphIndex: Int) {
        if (chapterContent.isNotEmpty() && paragraphIndex in chapterContent.indices) {
            paragraphOffset = 0
            lastWordStartInSegment = 0

            val text = chapterContent[paragraphIndex]
            speakInternal(text, paragraphIndex)
        } else {
            checkPlayNextChapter()
        }
    }

    fun stopReading(isFromService: Boolean = false) {
        tts.stop()
        paragraphOffset = 0
        lastWordStartInSegment = 0
        if (isFromService) {
            _events.tryEmit(TTSEvent.StopReading)
        }
    }

    fun pauseReading() {
        paragraphOffset = lastWordStartInSegment
        tts.stop()
    }

    fun resumeReading(paragraphIndex: Int) {
        if (chapterContent.isEmpty() || paragraphIndex !in chapterContent.indices) return

        val fullText = chapterContent[paragraphIndex]

        val resumeIndex = paragraphOffset.coerceIn(0, fullText.length)
        val textToSpeak = fullText.substring(resumeIndex)

        if (textToSpeak.isBlank()) {
            checkPLayNextParagraph()
            return
        }

        speakInternal(textToSpeak, paragraphIndex, baseOffset = resumeIndex)
    }

    private fun speakInternal(text: String, paragraphIndex: Int, baseOffset: Int = 0) {
        val chunks = splitTextIdeally(text)
        currentChunkOffsets.clear()

        var runningLength = baseOffset
        val uniqueId = UUID.randomUUID().toString()

        chunks.forEachIndexed { index, chunk ->
            currentChunkOffsets.add(runningLength)

            val utteranceId = "para_${paragraphIndex}_chunk_${index}_of_${chunks.size}_$uniqueId"

            tts.speak(
                chunk,
                if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD,
                null,
                utteranceId
            )

            runningLength += chunk.length
        }
    }

    private fun splitTextIdeally(text: String, maxLength: Int = 3900): List<String> {
        if (text.length <= maxLength) return listOf(text)

        val result = mutableListOf<String>()
        var remaining = text

        while (remaining.length > maxLength) {
            var splitIndex = -1

            val searchStart = (maxLength - 500).coerceAtLeast(0)
            for (i in maxLength downTo searchStart) {
                val c = remaining[i]
                if (c == '.' || c == '?' || c == '!') {
                    splitIndex = i
                    break
                }
            }

            if (splitIndex == -1) {
                splitIndex = remaining.lastIndexOf(' ', maxLength)
            }

            if (splitIndex == -1) {
                splitIndex = maxLength
            }

            val cut = (splitIndex + 1).coerceAtMost(remaining.length)
            result.add(remaining.take(cut))
            remaining = remaining.substring(cut)
        }

        if (remaining.isNotEmpty()) {
            result.add(remaining)
        }
        return result
    }

    suspend fun getAvailableVoicesAndLanguages() = withContext(Dispatchers.IO) {
        val languages = tts.availableLanguages?.toList()?.sortedBy { it.displayName } ?: emptyList()
        val voices = tts.voices?.filter { !it.isNetworkConnectionRequired }?.sortedBy { it.name } ?: emptyList()
        languages to voices
    }

    fun updateLanguage(locale: Locale) { tts.language = locale }
    fun updateSpeed(rate: Float) { tts.setSpeechRate(rate) }
    fun updatePitch(pitch: Float) { tts.setPitch(pitch) }
    fun updateVoice(voice: Voice) { tts.voice = voice }
    fun updateChapter(chapter: List<String>) { chapterContent = chapter }
    fun updateBookInfo(bookInfo: Book) { this.bookInfo = bookInfo }
    fun getTTS(): TextToSpeech { return tts }
}