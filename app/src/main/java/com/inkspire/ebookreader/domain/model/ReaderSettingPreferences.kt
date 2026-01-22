package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.ui.setting.bookmark.common.BookmarkStyle

@Immutable
data class ReaderSettingPreferences(
    val keepScreenOn: Boolean = false,
    val bookmarkStyle: BookmarkStyle = BookmarkStyle.WAVE_WITH_BIRDS
)