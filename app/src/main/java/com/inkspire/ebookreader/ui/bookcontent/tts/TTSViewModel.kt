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
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.Builder
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.usecase.MusicUseCase
import com.inkspire.ebookreader.domain.usecase.TTSContentUseCase
import com.inkspire.ebookreader.domain.usecase.TTSDatastoreUseCase
import com.inkspire.ebookreader.service.TTSService
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.htmlTagPattern
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.linkPattern
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class TTSViewModel(
    private val application: Application,
    private val ttsManager: TTSManager,
    private val datastoreUseCase: TTSDatastoreUseCase,
    private val contentUseCase: TTSContentUseCase,
    private val musicUseCase: MusicUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TTSPlaybackState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value
    )
    private val _currentHighlightRange = MutableStateFlow(TextRange.Zero)
    val currentHighlightRange = _currentHighlightRange.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TextRange.Zero
    )

    private val _currentReadingWordOffset = MutableStateFlow(0)
    val currentReadingWordOffset = _currentReadingWordOffset.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    private val _event = Channel<TTSUiEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private lateinit var bookInfo: Book
    private var currentChapterTitle: String = ""
    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaItemList = mutableListOf<MediaItem>()
    private val controllerListener = object : MediaController.Listener {
        override fun onDisconnected(controller: MediaController) {
            ttsManager.stopReading(true)
        }
    }
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (_state.value.isActivated) {
                if (!isPlaying && !_state.value.isPaused) {
                    ttsManager.pauseReading()
                    if (_state.value.musicPreferences.enable)
                        mediaController?.apply {
                            volume = _state.value.musicPreferences.volume
                        }
                    _state.update { it.copy(isPaused = true) }
                } else if (isPlaying && _state.value.isPaused) {
                    ttsManager.resumeReading(_state.value.paragraphIndex)
                    if (_state.value.musicPreferences.enable)
                        mediaController?.apply {
                            volume = 0.15f * _state.value.musicPreferences.volume
                        }
                    _state.update { it.copy(isPaused = false) }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            datastoreUseCase.ttsPreferences.collectLatest { prefs ->
                _state.update { it.copy(ttsPreferences = prefs) }

                ttsManager.updateSpeed(prefs.speed)
                ttsManager.updatePitch(prefs.pitch)

                val (languages, voices) = ttsManager.getAvailableVoicesAndLanguages()
                val selectedLocale = languages.find { it.displayName == prefs.locale } ?: Locale.getDefault()
                val selectedVoice = voices.find {
                    it.name == prefs.voice && it.locale == selectedLocale
                } ?: voices.firstOrNull { it.locale == selectedLocale } ?: ttsManager.getTTS().defaultVoice

                ttsManager.updateLanguage(selectedLocale)
                ttsManager.updateVoice(selectedVoice)
                _state.update { it.copy(currentVoiceQuality = selectedVoice.quality.toString()) }
            }
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

                            mediaController?.apply {
                                volume = 0.15f * _state.value.musicPreferences.volume
                            }
                        } else {
                            ttsManager.stopReading()
                            _state.update {
                                it.copy(
                                    isPaused = false,
                                    isActivated = false
                                )
                            }
                            _event.send(TTSUiEvent.StopReading)
                            if (_state.value.musicPreferences.enable)
                                mediaController?.apply {
                                    volume = _state.value.musicPreferences.volume
                                }
                            else
                                mediaController?.clearMediaItems()
                        }
                    }
                    TTSEvent.CheckPlayPreviousChapter -> {
                        if (_state.value.chapterIndex > 0) {
                            val prevIndex = _state.value.chapterIndex - 1
                            contentUseCase.saveBookInfoParagraphIndex(bookInfo.id, 0)
                            _state.update {
                                it.copy(
                                    isPaused = false,
                                    chapterIndex = prevIndex,
                                    paragraphIndex = 0
                                )
                            }

                            loadChapterData(
                                chapterIndex = prevIndex,
                                autoPlay = true
                            )

                            mediaController?.apply {
                                volume = 0.15f * _state.value.musicPreferences.volume
                            }
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

                    TTSEvent.CheckPauseReading -> {
                        mediaController?.apply {
                            pause()
                        }
                    }
                    TTSEvent.CheckResumeReading -> {
                        mediaController?.apply {
                            play()
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            musicUseCase.getSelectedMusicPaths().collectLatest { selectedItems ->
                mediaItemList.clear()
                mediaItemList.addAll(selectedItems.map { media3Item ->
                    if (::bookInfo.isInitialized)
                        Builder()
                            .setUri(media3Item.uri)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setArtworkUri(bookInfo.coverImagePath.toUri())
                                    .setTitle(bookInfo.title)
                                    .setArtist(currentChapterTitle)
                                    .build()
                            )
                            .build()
                    else
                        Builder()
                            .setUri(media3Item.uri)
                            .build()
                })
                if (mediaItemList.isNotEmpty()) {
                    mediaController?.apply {
                        if (_state.value.musicPreferences.enable) {
                            mediaItemList.shuffle()
                            setMediaItems(mediaItemList)
                            prepare()
                            play()
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.musicPreferences.collectLatest { musicPrefs ->
                _state.update { it.copy(musicPreferences = musicPrefs) }

                val targetVolume = if (_state.value.isActivated && !_state.value.isPaused) {
                    0.15f * musicPrefs.volume
                } else {
                    musicPrefs.volume
                }
                mediaController?.volume = targetVolume

                if (musicPrefs.enable && _state.value.isActivated && mediaItemList.isNotEmpty()) {
                    if (mediaController?.isPlaying == false) {
                        mediaController?.apply {
                            setMediaItems(mediaItemList)
                            prepare()
                            play()
                        }
                    }
                } else if (!musicPrefs.enable && _state.value.isActivated) {
                    val silentTrackMediaItem = Builder()
                        .setUri("asset:///silent.mp3".toUri())
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setArtworkUri(bookInfo.coverImagePath.toUri())
                                .setTitle(bookInfo.title)
                                .setArtist(currentChapterTitle)
                                .build()
                        ).build()
                    mediaController?.apply {
                        setMediaItems(listOf(silentTrackMediaItem))
                        volume = if (_state.value.isPaused)
                            _state.value.musicPreferences.volume
                        else
                            0.15f * _state.value.musicPreferences.volume
                    }
                } else if (mediaController?.isPlaying == true) {
                    mediaController?.apply {
                        stop()
                        clearMediaItems()
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
                    val nextIndex = _state.value.chapterIndex + 1
                    _state.update {
                        it.copy(
                            isPaused = false,
                            chapterIndex = nextIndex,
                            paragraphIndex = 0
                        )
                    }
                    viewModelScope.launch {
                        contentUseCase.saveBookInfoParagraphIndex(bookInfo.id, 0)
                        loadChapterData(
                            chapterIndex = nextIndex,
                            autoPlay = true
                        )
                    }
                    mediaController?.apply {
                        volume = 0.15f * _state.value.musicPreferences.volume
                    }
                } else {
                    ttsManager.stopReading()
                    _state.update {
                        it.copy(
                            isPaused = false,
                            isActivated = false
                        )
                    }
                    viewModelScope.launch {
                        _event.send(TTSUiEvent.StopReading)
                    }
                    if (_state.value.musicPreferences.enable)
                        mediaController?.apply {
                            volume = _state.value.musicPreferences.volume
                        }
                    else
                        mediaController?.clearMediaItems()
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
                    mediaController?.apply {
                        volume = 0.15f * _state.value.musicPreferences.volume
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
                        if (_state.value.musicPreferences.enable)
                            volume = 0.15f * _state.value.musicPreferences.volume
                        play()
                    }
                } else {
                    ttsManager.pauseReading()
                    _state.update { it.copy(isPaused = true) }
                    mediaController?.apply {
                        if (_state.value.musicPreferences.enable)
                            volume = _state.value.musicPreferences.volume
                        else
                            pause()
                    }
                }
            }
            TTSAction.OnPlayPreviousChapterClick -> {
                if (_state.value.chapterIndex > 0) {
                    val prevIndex = _state.value.chapterIndex - 1
                    _state.update {
                        it.copy(
                            isPaused = false,
                            chapterIndex = prevIndex,
                            paragraphIndex = 0
                        )
                    }
                    viewModelScope.launch {
                        contentUseCase.saveBookInfoParagraphIndex(bookInfo.id, 0)

                        loadChapterData(
                            chapterIndex = prevIndex,
                            autoPlay = true
                        )
                    }
                    mediaController?.apply {
                        volume = 0.15f * _state.value.musicPreferences.volume
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
                    mediaController?.apply {
                        volume = 0.15f * _state.value.musicPreferences.volume
                    }
                } else {
                    ttsManager.checkPlayPreviousChapter()
                }
            }
            TTSAction.OnStopClick -> {
                if (_state.value.isActivated) {
                    ttsManager.stopReading()
                    _state.update {
                        it.copy(
                            isPaused = false,
                            isActivated = false
                        )
                    }
                    if (_state.value.musicPreferences.enable)
                        mediaController?.apply {
                            volume = _state.value.musicPreferences.volume
                        }
                    else
                        mediaController?.clearMediaItems()
                }
            }
            is TTSAction.StartTTS -> {
                _state.update { it.copy(paragraphIndex = action.paragraphIndex)}
                if (mediaController == null) {
                    startService()
                } else {
                    prepareAndStart()
                }
            }

            is TTSAction.SetBookInfo -> {
                bookInfo = action.bookInfo
                ttsManager.updateBookInfo(bookInfo)
                updateMediaItemList()
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
                    val updatedMetadata = currentMediaItem
                        ?.mediaMetadata
                        ?.buildUpon()
                        ?.setArtworkUri(bookInfo.coverImagePath.toUri())
                        ?.setTitle(bookInfo.title)
                        ?.setArtist(ch.chapterTitle)
                        ?.build()
                    val updatedMediaItem = updatedMetadata?.let {
                        currentMediaItem?.buildUpon()?.setMediaMetadata(updatedMetadata)?.build()
                    }
                    updatedMediaItem?.let { replaceMediaItem(0, it) }
                }
            } ?: updateMediaItemList()

            if (autoPlay && _state.value.isActivated && !_state.value.isPaused) {
                delay(200)
                ttsManager.startReading(0)
            }
            contentUseCase.saveBookInfoChapterIndex(bookInfo.id, chapterIndex)
        }
    }

    private fun prepareAndStart() {
        viewModelScope.launch {
            if (_state.value.musicPreferences.enable) {
                mediaController?.apply {
                    volume = 0.15f * _state.value.musicPreferences.volume
                    if (!isPlaying){
                        setMediaItems(mediaItemList)
                        prepare()
                        play()
                    } else {
                        val updatedMetadata = currentMediaItem
                            ?.mediaMetadata
                            ?.buildUpon()
                            ?.setArtworkUri(bookInfo.coverImagePath.toUri())
                            ?.setTitle(bookInfo.title)
                            ?.setArtist(currentChapterTitle)
                            ?.build()
                        val updatedMediaItem = updatedMetadata?.let {
                            currentMediaItem?.buildUpon()?.setMediaMetadata(updatedMetadata)?.build()
                        }
                        updatedMediaItem?.let { replaceMediaItem(0, it) }
                    }
                }
            } else {
                val silentTrackMediaItem = Builder()
                    .setUri("asset:///silent.mp3".toUri())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtworkUri(bookInfo.coverImagePath.toUri())
                            .setTitle(bookInfo.title)
                            .setArtist(currentChapterTitle)
                            .build()
                    ).build()
                mediaController?.apply {
                    setMediaItems(listOf(silentTrackMediaItem))
                    prepare()
                    play()
                }
            }
            _state.update { it.copy(
                isActivated = true,
                isPaused = false
            )}
            ttsManager.startReading(_state.value.paragraphIndex)
        }
    }

    @OptIn(UnstableApi::class)
    private fun startService() {
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

    fun updateMediaItemList() {
        mediaItemList = mediaItemList.map { item ->
            val newMetadata = item.mediaMetadata
                .buildUpon()
                .setArtworkUri(bookInfo.coverImagePath.toUri())
                .setTitle(bookInfo.title)
                .setArtist(currentChapterTitle)
                .build()
            item.buildUpon()
                .setMediaMetadata(newMetadata)
                .build()
        }.toMutableList()
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