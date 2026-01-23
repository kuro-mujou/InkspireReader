package com.inkspire.ebookreader.domain.model

data class SearchResult(
    val bookId: String,
    val tocId: Int,
    val paragraphIndex: Int,
    val chapterTitle: String,
    val snippet: String,
    val matchWord: String,
    val isCaseSensitive: Boolean
)
