package com.inkspire.ebookreader.ui.setting

sealed interface SettingAction {
    data class OpenTTSVoiceMenu(val open: Boolean) : SettingAction
    data class KeepScreenOn(val keepScreenOn: Boolean) : SettingAction
    data class OpenAutoScrollMenu(val open: Boolean) : SettingAction
    data class OpenBackgroundMusicMenu(val open: Boolean) : SettingAction
    data class OpenBookmarkThemeMenu(val open: Boolean) : SettingAction
    data class OpenCategoryMenu(val open: Boolean) : SettingAction
    data class OpenHiddenTextMenu(val open: Boolean) : SettingAction
}