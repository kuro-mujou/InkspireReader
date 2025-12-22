package com.inkspire.ebookreader.ui.bookcontent.bottombar.tts

sealed interface BottomBarTTSAction {
    data object OnPreviousChapterClicked : BottomBarTTSAction
    data object OnPreviousParagraphClicked : BottomBarTTSAction
    data object OnPlayPauseClicked : BottomBarTTSAction
    data object OnNextParagraphClicked : BottomBarTTSAction
    data object OnNextChapterClicked : BottomBarTTSAction
    data object UpdateMusicMenuVisibility : BottomBarTTSAction
    data object OnStopClicked : BottomBarTTSAction
    data object UpdateVoiceMenuVisibility : BottomBarTTSAction
}