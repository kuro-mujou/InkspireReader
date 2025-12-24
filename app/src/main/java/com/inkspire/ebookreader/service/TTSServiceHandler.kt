package com.inkspire.ebookreader.service


import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

@UnstableApi
class TTSServiceHandler(
    private val context: Context,
    private val ttsManager: TTSManager,
) : Player.Listener {
    val ttsState = ttsManager.state
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (ttsState.value.isSpeaking) {
            if (isPlaying) {
                ttsManager.updateReadingState(false)
                ttsManager.resumeReading()
            } else {
                ttsManager.updateReadingState(true)
                ttsManager.pauseReading(false)
            }
        }
    }
}