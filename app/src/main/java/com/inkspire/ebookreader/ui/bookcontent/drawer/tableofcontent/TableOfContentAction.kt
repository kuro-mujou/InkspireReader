package com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent

sealed interface TableOfContentAction {
    data class UpdateTargetSearchIndex(val index: Int) : TableOfContentAction
    data class UpdateFirstVisibleTocIndex(val index: Int) : TableOfContentAction
    data class UpdateLastVisibleTocIndex(val index: Int) : TableOfContentAction
    data class ChangeFabVisibility(val visibility: Boolean) : TableOfContentAction
    data class UpdateSearchState(val searchState: Boolean) : TableOfContentAction
    data class UpdateCurrentChapterFavoriteState(
        val chapterIndex: Int,
        val isFavorite: Boolean
    ) : TableOfContentAction
}