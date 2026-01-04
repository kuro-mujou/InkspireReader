package com.inkspire.ebookreader.ui.bookcontent.bottombar.tts

sealed interface BottomBarTTSAction {
    data object UpdateMusicMenuVisibility : BottomBarTTSAction
    data object UpdateVoiceMenuVisibility : BottomBarTTSAction
}