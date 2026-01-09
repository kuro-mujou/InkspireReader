package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Chapter(
    val chapterContentId: Int = 0,
    val tocId: Int,
    val bookId: String,
    val chapterTitle: String,
    val content: List<String>,
    val highlights: Map<Int, List<Highlight>> = emptyMap()
)