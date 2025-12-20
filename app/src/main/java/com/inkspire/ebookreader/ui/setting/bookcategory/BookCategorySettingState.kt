package com.inkspire.ebookreader.ui.setting.bookcategory

import com.inkspire.ebookreader.domain.model.Category

data class BookCategorySettingState (
    val bookCategories: List<Category> = emptyList(),
)