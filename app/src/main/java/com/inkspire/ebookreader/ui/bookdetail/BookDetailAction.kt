package com.inkspire.ebookreader.ui.bookdetail

import com.inkspire.ebookreader.domain.model.Category

sealed interface BookDetailAction {
    data class OnDrawerItemClick(val index: Int) : BookDetailAction
    data object OnBookMarkClick : BookDetailAction
    data class ChangeChipState(val category: Category) : BookDetailAction
}