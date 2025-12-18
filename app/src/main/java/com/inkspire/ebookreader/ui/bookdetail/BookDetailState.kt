package com.inkspire.ebookreader.ui.bookdetail

import com.inkspire.ebookreader.data.model.BookWithCategories
import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.model.TableOfContent

data class BookDetailState(
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val bookWithCategories: BookWithCategories? = null,
    val tableOfContents: List<TableOfContent> = emptyList(),
    val isSortedByFavorite: Boolean = false,
    val categories: List<Category> = emptyList()
)
