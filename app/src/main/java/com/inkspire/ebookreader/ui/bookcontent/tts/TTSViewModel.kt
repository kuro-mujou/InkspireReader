package com.inkspire.ebookreader.ui.bookcontent.tts

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.ui.text.TextRange
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem.Builder
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.usecase.TTSContentUseCase
import com.inkspire.ebookreader.domain.usecase.TTSDatastoreUseCase
import com.inkspire.ebookreader.service.TTSService
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.htmlTagPattern
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.linkPattern
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class TTSViewModel(
    private val application: Application,
    private val ttsManager: TTSManager,
    private val datastoreUseCase: TTSDatastoreUseCase,
    private val contentUseCase: TTSContentUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TTSPlaybackState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )
    private val _currentHighlightRange = MutableStateFlow(TextRange.Zero)
    val currentHighlightRange = _currentHighlightRange.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TextRange.Zero
    )

    private val _currentReadingWordOffset = MutableStateFlow(0)
    val currentReadingWordOffset = _currentReadingWordOffset.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )
    private val _event = Channel<TTSUiEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()
    private lateinit var bookInfo: Book
    private var currentChapterTitle: String = ""
    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controllerListener = object : MediaController.Listener {
        override fun onDisconnected(controller: MediaController) {
            ttsManager.stopReading(true)
        }
    }
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (!isPlaying) {
                ttsManager.pauseReading()
                _state.update { it.copy(isPaused = true) }
            } else {
                ttsManager.resumeReading(_state.value.paragraphIndex)
                _state.update { it.copy(isPaused = false) }
            }
        }
    }

    init {
        viewModelScope.launch {
            combine(
                datastoreUseCase.getTtsSpeed(),
                datastoreUseCase.getTtsPitch(),
                datastoreUseCase.getTtsLocale(),
                datastoreUseCase.getTtsVoice()
            ) { speed, pitch, localeName, voiceName ->
                val (languages, voices) = ttsManager.getAvailableVoicesAndLanguages()
                val selectedLocale = languages.find { it.displayName == localeName } ?: Locale.getDefault()
                val selectedVoice = voices.find {
                    it.name == voiceName && it.locale == selectedLocale
                } ?: voices.firstOrNull { it.locale == selectedLocale } ?: ttsManager.getTTS().defaultVoice

                _state.update { it.copy(
                    currentSpeed = speed,
                    currentPitch = pitch,
                    currentLanguage = selectedLocale,
                    currentVoice = selectedVoice
                )}
                ttsManager.updateSpeed(speed)
                ttsManager.updatePitch(pitch)
                ttsManager.updateLanguage(selectedLocale)
                ttsManager.updateVoice(selectedVoice)
            }.collect()
        }
        viewModelScope.launch {
            ttsManager.events.collect { event ->
                when (event) {
                    TTSEvent.CheckPlayNextChapter -> {
                        if (_state.value.chapterIndex < bookInfo.totalChapter - 1) {
                            val nextIndex = _state.value.chapterIndex + 1

                            contentUseCase.saveBookInfoParagraphIndex(bookInfo.id, 0)
                            _state.update {
                                it.copy(
                                    isPaused = false,
                                    chapterIndex = nextIndex,
                                    paragraphIndex = 0
                                )
                            }

                            loadChapterData(
                                chapterIndex = nextIndex,
                                autoPlay = true
                            )
                        } else {
                            ttsManager.stopReading()
                        }
                    }
                    TTSEvent.CheckPlayPreviousChapter -> {
                        if (_state.value.chapterIndex > 0) {
                            val prevIndex = _state.value.chapterIndex - 1
                            contentUseCase.saveBookInfoParagraphIndex(bookInfo.id, 0)
                            _state.update {
                                it.copy(
                                    isPaused = false,
                                    chapterIndex = prevIndex, paragraphIndex = 0
                                )
                            }

                            loadChapterData(
                                chapterIndex = prevIndex,
                                autoPlay = true
                            )
                        }
                    }
                    TTSEvent.CheckPlayNextParagraph -> {
                        if (_state.value.paragraphIndex < _state.value.chapterText.size - 1) {
                            contentUseCase.saveBookInfoParagraphIndex(bookInfo.id, _state.value.paragraphIndex + 1)
                            _state.update { currentState ->
                                ttsManager.startReading(currentState.paragraphIndex + 1)
                                currentState.copy(
                                    isPaused = false,
                                    paragraphIndex = currentState.paragraphIndex + 1
                                )
                            }
                        } else {
                            ttsManager.checkPlayNextChapter()
                        }
                    }
                    TTSEvent.StopReading -> {
                        _state.update {
                            it.copy(
                                isPaused = false,
                                isActivated = false
                            )
                        }
                        mediaController?.clearMediaItems()
                        _event.send(TTSUiEvent.StopReading)
                    }

                    is TTSEvent.OnRangeStart -> {
                        _currentHighlightRange.value = TextRange(event.startOffset, event.endOffset)
                    }

                    is TTSEvent.OnReadOffset -> {
                        _currentReadingWordOffset.value = event.offset
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun onAction(action: TTSAction) {
        when (action) {
            TTSAction.OnPlayNextChapterClick -> {
                if (_state.value.chapterIndex < bookInfo.totalChapter - 1) {
                    _state.update {
                        it.copy(
                            isPaused = false,
                            chapterIndex = it.chapterIndex + 1,
                            paragraphIndex = 0
                        )
                    }
                } else {
                    ttsManager.stopReading()
                }
            }
            TTSAction.OnPlayNextParagraphClick -> {
                if (_state.value.paragraphIndex < _state.value.chapterText.size - 1) {
                    viewModelScope.launch {
                        contentUseCase.saveBookInfoParagraphIndex(bookInfo.id, _state.value.paragraphIndex + 1)
                    }
                    _state.update { currentState ->
                        ttsManager.startReading(currentState.paragraphIndex + 1)
                        currentState.copy(
                            isPaused = false,
                            paragraphIndex = currentState.paragraphIndex + 1
                        )
                    }
                } else {
                    ttsManager.checkPlayNextChapter()
                }
            }
            TTSAction.OnPlayPauseClick -> {
                if (_state.value.isPaused) {
                    ttsManager.resumeReading(_state.value.paragraphIndex)
                    _state.update { it.copy(isPaused = false) }
                    mediaController?.apply {
                        play()
                    }
                } else {
                    ttsManager.pauseReading()
                    _state.update { it.copy(isPaused = true) }
                    mediaController?.apply {
                        pause()
                    }
                }
            }
            TTSAction.OnPlayPreviousChapterClick -> {
                if (_state.value.chapterIndex > 0) {
                    _state.update {
                        it.copy(
                            isPaused = false,
                            chapterIndex = it.chapterIndex - 1,
                            paragraphIndex = 0
                        )
                    }
                } else {
                    ttsManager.startReading(0)
                }
            }
            TTSAction.OnPlayPreviousParagraphClick -> {
                if (_state.value.paragraphIndex > 0) {
                    viewModelScope.launch {
                        contentUseCase.saveBookInfoParagraphIndex(bookInfo.id, _state.value.paragraphIndex - 1)
                    }
                    _state.update { currentState ->
                        ttsManager.startReading(currentState.paragraphIndex - 1)
                        currentState.copy(
                            isPaused = false,
                            paragraphIndex = currentState.paragraphIndex - 1
                        )
                    }
                } else {
                    ttsManager.checkPlayPreviousChapter()
                }
            }
            TTSAction.OnStopClick -> {
                mediaController?.clearMediaItems()
                ttsManager.stopReading()
                _state.update {
                    it.copy(
                        isPaused = false,
                        isActivated = false
                    )
                }
            }
            is TTSAction.StartTTS -> {
                _state.update { it.copy(
                    paragraphIndex = action.paragraphIndex,
                    isActivated = true,
                    isPaused = false
                )}

                viewModelScope.launch {
                    val intent = Intent(application, TTSService::class.java)
                    Util.startForegroundService(application, intent)
                }

                val serviceComponentName = ComponentName(application, TTSService::class.java)
                val sessionToken = SessionToken(application, serviceComponentName)
                controllerFuture = MediaController
                    .Builder(application, sessionToken)
                    .setListener(controllerListener)
                    .buildAsync()
                controllerFuture?.addListener({
                    try {
                        mediaController = controllerFuture?.get()
                        mediaController?.addListener(playerListener)
                        prepareAndStart()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(application))
            }

            is TTSAction.SetBookInfo -> {
                bookInfo = action.bookInfo
                ttsManager.updateBookInfo(bookInfo)
            }

            is TTSAction.UpdateCurrentChapterData -> {
                val isSameChapter = _state.value.chapterIndex == action.realCurrentChapterIndex
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            chapterIndex = action.chapterIndexToLoadData,
                            paragraphIndex = 0
                        )
                    }

                    loadChapterData(
                        chapterIndex = action.chapterIndexToLoadData,
                        autoPlay = false
                    )

                    if (_state.value.isActivated && !isSameChapter) {
                        if (!_state.value.isPaused) {
                            ttsManager.stopReading()
                            delay(500)
                            ttsManager.startReading(_state.value.paragraphIndex)
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadChapterData(chapterIndex: Int, autoPlay: Boolean) {
        val chapter = contentUseCase.getChapterContent(bookInfo.id, chapterIndex)

        chapter?.let { ch ->
            currentChapterTitle = ch.chapterTitle
            val contentToRead = ch.content.map { raw ->
                val cleaned = htmlTagPattern.replace(raw, replacement = "")
                if (linkPattern.containsMatchIn(cleaned)) " " else cleaned.trim()
            }

            _state.update { it.copy(chapterText = contentToRead) }

            ttsManager.updateChapter(contentToRead)

            mediaController?.apply {
                if (_state.value.isActivated) {
                    val updatedMetadata = currentMediaItem?.mediaMetadata?.buildUpon()
                        ?.setArtist(ch.chapterTitle)?.build()
                    val updatedMediaItem = updatedMetadata?.let {
                        currentMediaItem?.buildUpon()?.setMediaMetadata(updatedMetadata)?.build()
                    }
                    updatedMediaItem?.let { replaceMediaItem(0, it) }
                }
            }

            if (autoPlay && _state.value.isActivated && !_state.value.isPaused) {
                delay(100)
                ttsManager.startReading(0)
            }
            contentUseCase.saveBookInfoChapterIndex(bookInfo.id, chapterIndex)
        }
    }

    private fun prepareAndStart() {
        val controller = mediaController ?: return
        viewModelScope.launch {
            val mediaItem = Builder()
                .setUri("asset:///silent.mp3".toUri())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtworkUri(bookInfo.coverImagePath.toUri())
                        .setTitle(bookInfo.title)
                        .setArtist(currentChapterTitle)
                        .build()
                )
                .build()

            if (controller.isPlaying) {
                controller.apply {
                    volume = 0.3f
                }
                ttsManager.startReading(_state.value.paragraphIndex)
            } else {
                controller.apply {
                    setMediaItems(listOf(mediaItem))
                    prepare()
                    play()
                    ttsManager.startReading(_state.value.paragraphIndex)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stopReading()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        mediaController?.clearMediaItems()
        mediaController?.removeListener(playerListener)
        mediaController?.release()
        mediaController = null
    }
}