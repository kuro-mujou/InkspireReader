package com.inkspire.ebookreader.ui.bookcontent.bottombar.common

sealed interface BottomBarMode {
    val isPersistent: Boolean

    data object Main : BottomBarMode { override val isPersistent = true }
    data object Theme : BottomBarMode { override val isPersistent = false }
    data object Settings : BottomBarMode { override val isPersistent = false }
    data object Tts : BottomBarMode { override val isPersistent = true }
    data object AutoScroll : BottomBarMode { override val isPersistent = true }
}