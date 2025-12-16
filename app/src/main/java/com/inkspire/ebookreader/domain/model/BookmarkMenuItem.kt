package com.inkspire.ebookreader.domain.model

import com.inkspire.ebookreader.common.BookmarkStyle

data class BookmarkMenuItem(
    val id: Int,
    val title: String,
    val bookmarkStyle: BookmarkStyle
)
