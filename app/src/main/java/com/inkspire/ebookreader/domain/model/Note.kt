package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Note(
    val noteId: Int = 0,
    val bookId: String,
    val tocId: Int,
    val contentId: Int,
    val noteBody: String,
    val noteInput: String,
    val timestamp: String
)
