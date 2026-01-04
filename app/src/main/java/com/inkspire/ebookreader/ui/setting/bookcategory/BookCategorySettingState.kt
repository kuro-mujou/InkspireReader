package com.inkspire.ebookreader.ui.setting.bookcategory

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.domain.model.Category

@Immutable
data class BookCategorySettingState (
    val bookCategories: List<Category> = emptyList(),
)