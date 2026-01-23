package com.inkspire.ebookreader.ui.bookcontent.topbar

import androidx.compose.runtime.Immutable

@Immutable
data class BookContentTopBarState(
    val topBarVisibility: Boolean = false,
    val showFindAndReplace: Boolean = false,
    val showHighlightList: Boolean = false,
    val showSearchResultsSheet: Boolean = false
)
