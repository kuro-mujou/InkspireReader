package com.inkspire.ebookreader.domain.model

data class TableOfContent(
    val tocId: Int? = null,
    val bookId: String,
    val title: String,
    val index: Int,
    val isFavorite: Boolean,
)