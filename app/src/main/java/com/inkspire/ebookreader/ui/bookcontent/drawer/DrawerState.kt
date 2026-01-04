package com.inkspire.ebookreader.ui.bookcontent.drawer

import androidx.compose.runtime.Immutable

@Immutable
data class DrawerState(
    val fromUser: Boolean = false,
    val visibility: Boolean = false,
    val isAnimating: Boolean = false,
    val selectedTabIndex: Int = 0
)