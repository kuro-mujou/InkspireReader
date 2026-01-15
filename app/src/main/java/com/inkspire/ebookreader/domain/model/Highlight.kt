package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Highlight(
    val id: Long,
    val paragraphIndex: Int,
    val startOffset: Int,
    val endOffset: Int,
    val colorIndex: Int,
    val createdTime: Long,
    val note: String? = null
)

@Immutable
data class HighlightToInsert(
    val bookId: String,
    val tocId: Int,
    val paragraphIndex: Int,
    val startOffset: Int,
    val endOffset: Int,
    val colorIndex: Int,
    val createdTime: Long,
    val note: String? = null
)
