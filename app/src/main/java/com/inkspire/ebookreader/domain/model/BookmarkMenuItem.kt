package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.ui.setting.bookmark.common.BookmarkStyle

@Immutable
data class BookmarkMenuItem(
    val id: Int,
    val title: String,
    val bookmarkStyle: BookmarkStyle
)
