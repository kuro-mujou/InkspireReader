package com.inkspire.ebookreader.ui.setting.autoscroll

data class AutoScrollState(
    val currentScrollSpeed: Int = 10000,
    val delayAtStart: Int = 3000,
    val delayAtEnd: Int = 3000,
    val delayResumeMode: Int = 1000,
    val isAutoResumeScrollMode: Boolean = false,
)
