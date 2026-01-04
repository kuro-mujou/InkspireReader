package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ImagePath(
    val bookId: String,
    val imagePath: String
)
