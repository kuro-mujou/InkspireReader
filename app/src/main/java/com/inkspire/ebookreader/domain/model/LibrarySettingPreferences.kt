package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class LibrarySettingPreferences(
    val isSortedByFavorite: Boolean = true,
    val bookListViewType: Int = 1
)