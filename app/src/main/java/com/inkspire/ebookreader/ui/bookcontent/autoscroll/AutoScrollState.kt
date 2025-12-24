package com.inkspire.ebookreader.ui.bookcontent.autoscroll

data class AutoScrollState(
    val isActivated: Boolean = false,
    val isPaused: Boolean = true,
    val isScrolledToEnd: Boolean = false,
    val isAnimationRunning: Boolean = false,
    //setting info
    val autoScrollSpeed: Int = 0,
    val delayTimeAtStart: Int = 0,
    val delayTimeAtEnd: Int = 0,
    val autoScrollResumeDelayTime: Int = 0,
    val autoScrollResumeMode: Boolean = false
)
