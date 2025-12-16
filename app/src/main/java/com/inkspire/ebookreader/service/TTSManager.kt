package com.inkspire.ebookreader.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class TTSManager(
    private val context: Context
) {
    private val _state = MutableStateFlow(TtsPlaybackState())
    val state = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var textToSpeech: TextToSpeech? = null
    private var exoPlayer: Player? = null
    private var audioManager: AudioManager? = null
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                resumeReading()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pauseReading(abandonFocus = false)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                pauseReading(abandonFocus = true)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                pauseReading(abandonFocus = false)
            }
        }
    }
    private val playbackAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    private val focusRequest: AudioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
        .setAudioAttributes(playbackAttributes!!)
        .setAcceptsDelayedFocusGain(true)
        .setOnAudioFocusChangeListener(audioFocusChangeListener)
        .build()

    private var currentChapterParagraphs: List<String> = emptyList()
    private var totalChapters: Int = 0

    private var currentBaseOffset = 0

    var onChapterFinish: ((Int) -> Unit)? = null

    init {
        initializeTts()
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                _state.update { it.copy(isLoading = false) }
            }
        }

        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _state.update { it.copy(isSpeaking = true, isPaused = false) }
            }

            override fun onDone(utteranceId: String?) {
                currentBaseOffset = 0
                scope.launch {
                    playNextParagraphOrChapter()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _state.update { it.copy(isSpeaking = false) }
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                super.onRangeStart(utteranceId, start, end, frame)
                val absolutePosition = currentBaseOffset + start

                _state.update { it.copy(charOffset = absolutePosition) }
            }
        })
    }

    fun attachSystemComponents(player: Player?) {
        this.exoPlayer = player
    }

    fun setChapterContent(chapterIndex: Int, paragraphs: List<String>, totalChapters: Int) {
        this.currentChapterParagraphs = paragraphs
        this.totalChapters = totalChapters
        _state.update { it.copy(chapterIndex = chapterIndex) }
    }

    fun startReading(paragraphIndex: Int) {
        if (_state.value.isLoading) return
        if (requestAudioFocus()) {
            playParagraph(paragraphIndex, startOffset = 0)
        }
    }

    fun pauseReading(abandonFocus: Boolean = false) {
        textToSpeech?.stop()
        exoPlayer?.pause()
        _state.update { it.copy(isPaused = true, isSpeaking = false) }

        if (abandonFocus) {
            audioManager?.abandonAudioFocusRequest(focusRequest!!)
        }
    }

    fun resumeReading() {
        val currentState = _state.value
        if (currentState.chapterIndex != -1 && currentState.paragraphIndex != -1) {
            if (requestAudioFocus()) {
                exoPlayer?.play()
                playParagraph(
                    index = currentState.paragraphIndex,
                    startOffset = currentState.charOffset
                )
            }
        }
    }

    fun stopReading() {
        textToSpeech?.stop()
        exoPlayer?.stop()
        audioManager?.abandonAudioFocusRequest(focusRequest!!)
        _state.update {
            it.copy(isSpeaking = false, isPaused = false, paragraphIndex = -1, charOffset = 0)
        }
        currentBaseOffset = 0
    }

    fun nextChapter() = playNextChapter()
    fun prevChapter() = playPreviousChapter()

    private fun playParagraph(index: Int, startOffset: Int) {
        if (index in currentChapterParagraphs.indices) {
            val fullText = currentChapterParagraphs[index]

            val safeOffset = startOffset.coerceIn(0, fullText.length)

            currentBaseOffset = safeOffset

            val textToSpeak = fullText.substring(safeOffset)

            _state.update {
                it.copy(
                    paragraphIndex = index,
                    currentParagraphText = fullText,
                    charOffset = safeOffset
                )
            }

            if (exoPlayer?.isPlaying == true) {
                exoPlayer?.volume = 0.3f
            }

            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "id_$index")

            textToSpeech?.speak(
                textToSpeak,
                TextToSpeech.QUEUE_FLUSH,
                params,
                "id_$index"
            )
        } else {
            stopReading()
        }
    }

    private fun playNextParagraphOrChapter() {
        val currentIndex = _state.value.paragraphIndex
        if (currentIndex < currentChapterParagraphs.size - 1) {
            playParagraph(currentIndex + 1, startOffset = 0)
        } else {
            playNextChapter()
        }
    }

    private fun playPreviousParagraphOrChapter() {
        val currentIndex = _state.value.paragraphIndex
        if (currentIndex > 0) {
            playParagraph(currentIndex - 1, startOffset = 0)
        } else {
            playPreviousChapter()
        }
    }

    private fun playNextChapter() {
        val currentChapter = _state.value.chapterIndex
        if (currentChapter + 1 < totalChapters) {
            textToSpeech?.stop()
            onChapterFinish?.invoke(currentChapter + 1)
        } else {
            stopReading()
        }
    }

    private fun playPreviousChapter() {
        val currentChapter = _state.value.chapterIndex
        if (currentChapter - 1 >= 0) {
            textToSpeech?.stop()
            onChapterFinish?.invoke(currentChapter - 1)
        } else {
            stopReading()
        }
    }

    private fun requestAudioFocus(): Boolean {
        val res = audioManager?.requestAudioFocus(focusRequest!!)
        return res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun updateLanguage(locale: Locale) { textToSpeech?.language = locale }
    fun updateSpeed(rate: Float) { textToSpeech?.setSpeechRate(rate) }
    fun updatePitch(pitch: Float) { textToSpeech?.setPitch(pitch) }
    fun updateVoice(voice: Voice) { textToSpeech?.voice = voice }
    fun getTTS(): TextToSpeech? { return textToSpeech }

    fun shutdown() {
        textToSpeech?.shutdown()
        textToSpeech = null
        exoPlayer?.release()
        exoPlayer = null
    }
}