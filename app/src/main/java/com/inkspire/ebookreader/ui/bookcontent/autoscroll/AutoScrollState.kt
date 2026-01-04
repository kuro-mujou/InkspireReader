package com.inkspire.ebookreader.ui.bookcontent.autoscroll

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.domain.model.AutoScrollPreferences

@Immutable
data class AutoScrollState(
    val autoScrollPreferences: AutoScrollPreferences = AutoScrollPreferences(),

    val isActivated: Boolean = false,
    val isPaused: Boolean = true,
    val isScrolledToEnd: Boolean = false,
    val isAnimationRunning: Boolean = false,
)