package com.inkspire.ebookreader.ui.bookcontent.bottombar

import com.inkspire.ebookreader.ui.bookcontent.bottombar.common.BottomBarMode

data class BookContentBottomBarState(
    val bottomBarVisibility: Boolean = false,
    val bottomBarMode: BottomBarMode = BottomBarMode.Main
)
