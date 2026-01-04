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
import java.text.BreakIterator
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
    private var isActivated: Boolean = false

    private data class ChunkMetadata(
        val textToSpeak: String,
        val audioStartOffset: Int,
        val sentenceStart: Int,
        val sentenceEnd: Int
    )

    private val currentChunks = mutableListOf<ChunkMetadata>()

    private var currentHighlightedSentenceStart: Int = -1

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    updateHighlightAndResume(utteranceId, 0)
                }

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
                    updateHighlightAndResume(utteranceId, start)
                }
            })
        }
    }

    private fun updateHighlightAndResume(utteranceId: String?, wordStartInUtterance: Int) {
        if (utteranceId != null && utteranceId.startsWith("para_")) {
            try {
                val parts = utteranceId.split("_")
                val chunkIndex = parts[3].toInt()
                val metadata = currentChunks.getOrNull(chunkIndex) ?: return

                val absoluteWordOffset = metadata.audioStartOffset + wordStartInUtterance

                lastWordStartInSegment = absoluteWordOffset

                _events.tryEmit(TTSEvent.OnReadOffset(absoluteWordOffset))

                if (currentHighlightedSentenceStart != metadata.sentenceStart) {
                    currentHighlightedSentenceStart = metadata.sentenceStart
                    _events.tryEmit(TTSEvent.OnRangeStart(metadata.sentenceStart, metadata.sentenceEnd))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    fun checkPause() {
        _events.tryEmit(TTSEvent.CheckPauseReading)
    }

    fun checkResume() {
        _events.tryEmit(TTSEvent.CheckResumeReading)
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

        if (resumeIndex >= fullText.length && fullText.isNotEmpty()) {
            checkPLayNextParagraph()
            return
        }

        speakInternal(fullText, paragraphIndex, baseOffset = resumeIndex)
    }

    private fun speakInternal(text: String, paragraphIndex: Int, baseOffset: Int = 0) {
        currentChunks.clear()
        currentHighlightedSentenceStart = -1

        val targetLocale = tts.voice?.locale ?: Locale.getDefault()
        val iterator = BreakIterator.getSentenceInstance(targetLocale)
        iterator.setText(text)

        var start = iterator.first()
        var end = iterator.next()

        while (end != BreakIterator.DONE) {
            if (end > baseOffset) {
                val audioStart = if (start < baseOffset) baseOffset else start

                val textToSpeak = text.substring(audioStart, end)

                if (textToSpeak.isNotBlank()) {
                    currentChunks.add(
                        ChunkMetadata(
                            textToSpeak = textToSpeak,
                            audioStartOffset = audioStart,
                            sentenceStart = start,
                            sentenceEnd = end
                        )
                    )
                }
            }
            start = end
            end = iterator.next()
        }

        if (currentChunks.isEmpty()) {
            checkPLayNextParagraph()
            return
        }

        val uniqueId = UUID.randomUUID().toString()
        val totalChunks = currentChunks.size

        currentChunks.forEachIndexed { index, metadata ->
            val utteranceId = "para_${paragraphIndex}_sent_${index}_of_${totalChunks}_$uniqueId"

            tts.speak(
                metadata.textToSpeak,
                if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD,
                null,
                utteranceId
            )
        }
    }

    suspend fun getAvailableVoicesAndLanguages() = withContext(Dispatchers.IO) {
        val languages = tts.availableLanguages?.toList()?.sortedBy { it.displayName } ?: emptyList()
        val voices = tts.voices?.filter { !it.isNetworkConnectionRequired }?.sortedBy { it.name } ?: emptyList()
        languages to voices
    }

    fun updateTTSActivated(isActivated: Boolean) { this.isActivated = isActivated }
    fun updateLanguage(locale: Locale) { tts.language = locale }
    fun updateSpeed(rate: Float) { tts.setSpeechRate(rate) }
    fun updatePitch(pitch: Float) { tts.setPitch(pitch) }
    fun updateVoice(voice: Voice) { tts.voice = voice }
    fun updateChapter(chapter: List<String>) { chapterContent = chapter }
    fun updateBookInfo(bookInfo: Book) { this.bookInfo = bookInfo }
    fun getTTS(): TextToSpeech { return tts }
    fun getIsTTSActivated(): Boolean = this.isActivated
}