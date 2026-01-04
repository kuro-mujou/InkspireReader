package com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent

import androidx.compose.runtime.Immutable

@Immutable
data class TableOfContentState(
    val targetSearchIndex: Int = -1,
    val firstVisibleTocIndex: Int = 0,
    val lastVisibleTocIndex: Int = 0,
    val fabVisibility: Boolean = false,
    val searchState: Boolean = false
)
