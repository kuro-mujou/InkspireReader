package com.inkspire.ebookreader.domain.model

data class HighlightResult(
    val bookId: String,
    val tocId: Int,
    val paragraphIndex: Int,
    val chapterTitle: String,
    val content: String,
    val highlights: List<Highlight>
)