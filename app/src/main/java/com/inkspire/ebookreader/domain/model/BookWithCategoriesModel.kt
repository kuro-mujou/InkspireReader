package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class BookWithCategoriesModel(
    val id: String,
    val title: String,
    val coverImagePath: String,
    val authors: List<String>,
    val description: String?,
    val totalChapter: Int,
    val currentChapter: Int,
    val currentParagraph: Int,
    val isRecentRead: Int = 0,
    val isFavorite: Boolean = false,
    val storagePath: String?,
    val isEditable: Boolean,
    val fileType: String,
    val categories: List<Category>
)