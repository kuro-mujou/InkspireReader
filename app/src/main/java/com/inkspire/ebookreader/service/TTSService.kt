package com.inkspire.ebookreader.service

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSManager
import org.koin.android.ext.android.inject

@UnstableApi
class TTSService : MediaSessionService() {

    private val ttsManager by inject<TTSManager>()
    private var audioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    ttsManager.checkResume()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    ttsManager.checkPause()
                }

                AudioManager.AUDIOFOCUS_LOSS -> {
                    ttsManager.checkPause()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    ttsManager.checkPause()
                }
            }
        }
    private val playbackAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
        .setAudioAttributes(playbackAttributes)
        .setAcceptsDelayedFocusGain(true)
        .setOnAudioFocusChangeListener(audioFocusChangeListener)
        .build()

    private lateinit var audioManager: AudioManager
    private var mediaSession: MediaSession? = null

    private val customCommandStop = SessionCommand(ACTION_STOP, Bundle.EMPTY)
    private val customCommandNext = SessionCommand(ACTION_NEXT, Bundle.EMPTY)
    private val customCommandPrevious = SessionCommand(ACTION_PREVIOUS, Bundle.EMPTY)

    override fun onCreate() {
        super.onCreate()

        val stopButton = CommandButton.Builder(CommandButton.ICON_STOP)
            .setCustomIconResId(R.drawable.ic_notification_stop)
            .setDisplayName("Stop")
            .setSessionCommand(customCommandStop)
            .build()
        val nextButton = CommandButton.Builder(CommandButton.ICON_SKIP_FORWARD)
            .setCustomIconResId(R.drawable.ic_notification_skip_to_next)
            .setDisplayName("Next Chapter")
            .setSessionCommand(customCommandNext)
            .build()
        val previousButton = CommandButton.Builder(CommandButton.ICON_SKIP_BACK)
            .setCustomIconResId(R.drawable.ic_notification_skip_to_back)
            .setDisplayName("Previous Chapter")
            .setSessionCommand(customCommandPrevious)
            .build()

        val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, false)
            .setHandleAudioBecomingNoisy(true)
            .setPauseAtEndOfMediaItems(false)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ALL
                shuffleModeEnabled = true
            }

        val forwardingPlayer = object : ForwardingPlayer(player) {
            override fun getAvailableCommands(): Player.Commands {
                return super.getAvailableCommands()
                    .buildUpon()
                    .remove(COMMAND_SEEK_TO_NEXT)
                    .remove(COMMAND_SEEK_TO_PREVIOUS)
                    .remove(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                    .build()
            }
        }

        mediaSession = MediaSession.Builder(this, forwardingPlayer)
            .setCallback(MyCallback())
            .setCustomLayout(
                ImmutableList.of(previousButton, nextButton, stopButton)
            )
            .build()

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.requestAudioFocus(focusRequest)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        audioManager.abandonAudioFocusRequest(focusRequest)
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

    private inner class MyCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailablePlayerCommands(
                    MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                        .remove(Player.COMMAND_SEEK_TO_NEXT)
                        .remove(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                        .remove(Player.COMMAND_SEEK_TO_PREVIOUS)
                        .remove(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                        .remove(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                        .build()
                )
                .setAvailableSessionCommands(
                    MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                        .add(customCommandPrevious)
                        .add(customCommandNext)
                        .add(customCommandStop)
                        .build()
                )
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            return when (customCommand.customAction) {
                ACTION_STOP -> {
                    ttsManager.stopReading(true)
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }

                ACTION_NEXT -> {
                    ttsManager.checkPlayNextChapter()
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }

                ACTION_PREVIOUS -> {
                    ttsManager.checkPlayPreviousChapter()
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }

                else -> {
                    super.onCustomCommand(session, controller, customCommand, args)
                }
            }
        }
    }

    companion object {
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
    }
}