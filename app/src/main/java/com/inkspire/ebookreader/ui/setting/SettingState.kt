package com.inkspire.ebookreader.ui.setting

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.domain.model.ReaderSettingPreferences

@Immutable
data class SettingState(
    val readerSettings: ReaderSettingPreferences = ReaderSettingPreferences(),

    val openCategoryMenu: Boolean = false,
    val openBackgroundMusicMenu: Boolean = false,
    val openBookmarkThemeMenu: Boolean = false,
    val openAutoScrollMenu: Boolean = false,
    val openTTSVoiceMenu: Boolean = false,
    val openHiddenTextMenu: Boolean = false,
)