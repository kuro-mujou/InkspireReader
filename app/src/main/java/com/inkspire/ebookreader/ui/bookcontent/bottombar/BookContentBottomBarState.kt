package com.inkspire.ebookreader.ui.bookcontent.bottombar

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.ui.bookcontent.bottombar.common.BottomBarMode

@Immutable
data class BookContentBottomBarState(
    val bottomBarVisibility: Boolean = false,
    val bottomBarMode: BottomBarMode = BottomBarMode.Main
)
