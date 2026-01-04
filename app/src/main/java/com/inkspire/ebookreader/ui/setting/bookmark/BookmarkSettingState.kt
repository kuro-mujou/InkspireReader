package com.inkspire.ebookreader.ui.setting.bookmark

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.domain.model.ReaderSettingPreferences

@Immutable
data class BookmarkSettingState(
    val readerSettings: ReaderSettingPreferences = ReaderSettingPreferences()
)
