package com.inkspire.ebookreader.ui.bookdetail

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.data.database.model.BookWithCategories
import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.model.TableOfContent

@Immutable
data class BookDetailState(
    val isShowCategoryMenu: Boolean = false,
    val bookWithCategories: BookWithCategories? = null,
    val tableOfContents: List<TableOfContent> = emptyList(),
    val categories: List<Category> = emptyList()
)
