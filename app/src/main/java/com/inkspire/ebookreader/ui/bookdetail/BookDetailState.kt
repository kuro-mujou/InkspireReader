package com.inkspire.ebookreader.ui.bookdetail

import com.inkspire.ebookreader.data.model.BookWithCategories
import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.model.TableOfContent

data class BookDetailState(
    val isShowCategoryMenu: Boolean = false,
    val bookWithCategories: BookWithCategories? = null,
    val tableOfContents: List<TableOfContent> = emptyList(),
    val categories: List<Category> = emptyList()
)
