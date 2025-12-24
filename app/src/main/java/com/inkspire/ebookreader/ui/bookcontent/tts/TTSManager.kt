package com.inkspire.ebookreader.ui.bookcontent.tts

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.inkspire.ebookreader.domain.model.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class TTSManager(
    private val context: Context
) {
    private val _state = MutableStateFlow(TTSPlaybackState())
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
    private var  mediaItemPreview: String = ""
    private var  mediaItemTitle: String = ""
    private var  mediaItemArtist: String = ""
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
                textToSpeech?.language = Locale.getDefault()
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

    fun setChapterContent(chapterIndex: Int, chapterTitle: String, paragraphs: List<String>) {
        this.currentChapterParagraphs = paragraphs
        this.mediaItemArtist = chapterTitle
        _state.update { it.copy(chapterIndex = chapterIndex) }
        exoPlayer?.let { player ->
            if (_state.value.isSpeaking) {
                val updatedMetadata = player.currentMediaItem?.mediaMetadata?.buildUpon() ?.setArtist(chapterTitle)?.build()
                val updatedMediaItem = updatedMetadata?.let{
                    player.currentMediaItem?.buildUpon()?.setMediaMetadata(updatedMetadata) ?.build()
                }
                updatedMediaItem?.let {
                    player.replaceMediaItem(0, it)
                }
                player.prepare()
                player.play()
            }
        }
    }

    fun startReading(paragraphIndex: Int) {
        if (_state.value.isLoading) return
        if (requestAudioFocus()) {
            playParagraph(paragraphIndex, startOffset = 0)
        }
    }

    fun pauseReading(abandonFocus: Boolean = false) {
        textToSpeech?.stop()
        _state.update { it.copy(isPaused = true, isSpeaking = false) }

        if (abandonFocus) {
            audioManager?.abandonAudioFocusRequest(focusRequest)
        }
    }

    fun resumeReading() {
        val currentState = _state.value
        if (currentState.chapterIndex != -1 && currentState.paragraphIndex != -1) {
            if (requestAudioFocus()) {
                playParagraph(
                    index = currentState.paragraphIndex,
                    startOffset = currentState.charOffset
                )
            }
        }
    }

    fun stopReading() {
        textToSpeech?.stop()
        audioManager?.abandonAudioFocusRequest(focusRequest)
        _state.update {
            it.copy(isSpeaking = false, isPaused = false, paragraphIndex = -1, charOffset = 0)
        }
        currentBaseOffset = 0
    }

    fun nextChapter() = playNextChapter()
    fun prevChapter() = playPreviousChapter()
    fun nextParagraph() = playNextParagraphOrChapter()
    fun prevParagraph() = playPreviousParagraphOrChapter()

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

            exoPlayer?.let { player ->
                if (!player.isPlaying) {
                    val mediaItem = MediaItem.Builder()
                        .setUri("asset:///silent.mp3".toUri())
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setArtworkUri(mediaItemPreview.toUri())
                                .setTitle(mediaItemTitle)
                                .setArtist(mediaItemArtist)
                                .build()
                        )
                        .build()
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                } else {
                    player.volume = 0.3f
                }
            }

            textToSpeech?.speak(
                textToSpeak,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "id_${_state.value.chapterIndex}_${_state.value.paragraphIndex}"
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
        val res = audioManager?.requestAudioFocus(focusRequest)
        return res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    suspend fun getAvailableVoicesAndLanguages() = withContext(Dispatchers.IO) {
        val tts = textToSpeech ?: return@withContext emptyList<Locale>() to emptyList<Voice>()
        val languages = tts.availableLanguages?.toList()?.sortedBy { it.displayName } ?: emptyList()
        val voices = tts.voices?.filter { !it.isNetworkConnectionRequired }?.sortedBy { it.name }
            ?: emptyList()
        languages to voices
    }

    fun updateLanguage(locale: Locale) { textToSpeech?.language = locale }
    fun updateSpeed(rate: Float) { textToSpeech?.setSpeechRate(rate) }
    fun updatePitch(pitch: Float) { textToSpeech?.setPitch(pitch) }
    fun updateVoice(voice: Voice) { textToSpeech?.voice = voice }
    fun updateBookInfo(bookInfo: Book){
        mediaItemPreview = bookInfo.coverImagePath
        mediaItemTitle = bookInfo.title
        totalChapters = bookInfo.totalChapter
    }
    fun updateReadingState(isPaused: Boolean) {
        _state.update { it.copy(isPaused = isPaused) }
    }
    fun getTTS(): TextToSpeech? { return textToSpeech }

    fun shutdown() {
        textToSpeech?.shutdown()
        textToSpeech = null
        exoPlayer?.release()
        exoPlayer = null
    }
}