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
    private val context: Context
)  : TextToSpeech.OnInitListener{
    private val tts: TextToSpeech = TextToSpeech(context, this)

    private val _events = MutableSharedFlow<TTSEvent>(
        extraBufferCapacity = 64
    )
    val events: SharedFlow<TTSEvent> = _events.asSharedFlow()
    private lateinit var bookInfo: Book
    private var chapterContent: List<String> = emptyList()
    private var currentUtteranceParagraphIndex: Int = -1
    private var lastRangeStart: Int = 0
    private var lastRangeEnd: Int = 0

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {

                }
                override fun onDone(utteranceId: String?) {
                    checkPLayNextParagraph()
                }
                override fun onError(utteranceId: String?) {

                }
                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    super.onRangeStart(utteranceId, start, end, frame)
                    val paragraphIndex = parseParagraphIndex(utteranceId)
                    if (paragraphIndex >= 0) {
                        currentUtteranceParagraphIndex = paragraphIndex
                        lastRangeStart = start
                        lastRangeEnd = end
                    }

//                    _events.tryEmit(
//                        TTSEvent.Range(
//                            paragraphIndex = paragraphIndex,
//                            start = start,
//                            end = end
//                        )
//                    )
                }
            })
        }
    }

    private fun parseParagraphIndex(utteranceId: String?): Int {
        if (utteranceId == null) return -1
        return utteranceId.substringAfterLast("_").toIntOrNull() ?: -1
    }

    fun checkPlayNextChapter() {
        _events.tryEmit(TTSEvent.CheckPlayNextChapter)
    }

    fun checkPlayPreviousChapter() {
        _events.tryEmit(TTSEvent.CheckPlayPreviousChapter)
    }

    fun checkPLayNextParagraph() {
        _events.tryEmit(TTSEvent.CheckPlayNextParagraph)
    }

    fun startReading(paragraphIndex: Int) {
        if (chapterContent.isNotEmpty() && paragraphIndex < chapterContent.size) {
            currentUtteranceParagraphIndex = paragraphIndex
            lastRangeStart = 0
            lastRangeEnd = 0
            val text = chapterContent[paragraphIndex]
            tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "paragraph_${UUID.randomUUID()}_${paragraphIndex}"
            )
        } else {
            checkPlayNextChapter()
        }
    }

    fun stopReading(isFromService: Boolean = false) {
        tts.stop()
        currentUtteranceParagraphIndex = -1
        lastRangeStart = 0
        lastRangeEnd = 0
        if (isFromService) {
            _events.tryEmit(TTSEvent.StopReading)
        }
    }

    fun resumeReading(paragraphIndex: Int) {
        if (chapterContent.isEmpty()) return
        if (paragraphIndex !in chapterContent.indices) return

        val fullText = chapterContent[paragraphIndex]

        val resumeFrom = if (
            paragraphIndex == currentUtteranceParagraphIndex &&
            lastRangeStart in 1 until fullText.length
        ) {
            lastRangeStart
        } else {
            0
        }

        val textToSpeak = fullText.substring(resumeFrom)

        tts.speak(
            textToSpeak,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "paragraph_${UUID.randomUUID()}_${paragraphIndex}"
        )
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