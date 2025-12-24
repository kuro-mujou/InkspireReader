package com.inkspire.ebookreader.ui.bookcontent.bottombar.tts

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import androidx.annotation.OptIn
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
import com.inkspire.ebookreader.service.TTSService
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.htmlTagPattern
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.linkPattern
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BottomBarTTSViewModel(
    private val application: Application,
    private val contentUseCase : TTSContentUseCase,
    private val ttsManager: TTSManager,
    private val bookId: String
) : ViewModel() {
    private val _state = MutableStateFlow(BottomBarTTSState())
    val state: StateFlow<BottomBarTTSState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controllerListener = object : MediaController.Listener {
        override fun onDisconnected(controller: MediaController) {
            // The service has crashed, been killed, or stopSelf() was called.
//            _uiState.update { it.copy(isPlaying = false) }
            // Attempt to reconnect or clean up UI
        }
    }
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (!isPlaying) {
                //stop tts manager
            } else {
                //resume tts manager
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun onAction(action: BottomBarTTSAction) {
        when (action) {
            is BottomBarTTSAction.StartTTS -> {
                val intent = Intent(application, TTSService::class.java)
                Util.startForegroundService(application, intent)

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
                        mediaController?.prepare()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(application))

                prepareAndStart(action.bookInfo, action.chapterIndex, action.paragraphIndex)
            }
            is BottomBarTTSAction.UpdateMusicMenuVisibility -> {
                _state.update { it.copy(ttsMusicMenuVisibility = !_state.value.ttsMusicMenuVisibility) }
            }

            is BottomBarTTSAction.UpdateVoiceMenuVisibility -> {
                _state.update { it.copy(ttsVoiceMenuVisibility = !_state.value.ttsVoiceMenuVisibility) }
            }

            else -> {}
        }
    }

    private fun prepareAndStart(bookInfo: Book, chapterIndex: Int, paragraphIndex: Int) {
        viewModelScope.launch {
            val chapter = contentUseCase.getChapterContent(bookInfo.id, chapterIndex)
            chapter?.let {
                val mediaItem = Builder()
                    .setUri("asset:///silent.mp3".toUri())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtworkUri(bookInfo.coverImagePath.toUri())
                            .setTitle(bookInfo.title)
                            .setArtist(chapter.chapterTitle)
                            .build()
                    )
                    .build()

                if (mediaController?.isPlaying!!) {
                    mediaController?.apply {
                        volume = 0.3f
                    }
                    ttsManager.startReading(
                        paragraphIndex = paragraphIndex,
                    )
                } else {
                    mediaController?.apply {
                        setMediaItems(listOf(mediaItem))
                        prepare()
                        play()
                        ttsManager.startReading(
                            paragraphIndex = paragraphIndex,
                        )
                    }
                }
                val contentToRead = it.content.map { raw ->
                    val cleaned = htmlTagPattern.replace(raw, replacement = "")
                    if (linkPattern.containsMatchIn(cleaned)) {
                        " "
                    } else {
                        cleaned.trim()
                    }
                }
//                ttsManager.setChapterContent(chapterIndex, it.chapterTitle, contentToRead)
//                ttsManager.startReading(paragraphIndex)
            }
        }
    }
}