package com.inkspire.ebookreader.ui.setting

data class SettingState(
    val keepScreenOn: Boolean = false,

    val openCategoryMenu: Boolean = false,
    val openBackgroundMusicMenu: Boolean = false,
    val openBookmarkThemeMenu: Boolean = false,
    val openAutoScrollMenu: Boolean = false,
    val openTTSVoiceMenu: Boolean = false,
)