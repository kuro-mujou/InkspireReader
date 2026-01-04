package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AutoScrollPreferences(
    val speed: Int = 20000,
    val delayTimeAtStart: Int = 3000,
    val delayTimeAtEnd: Int = 3000,
    val resumeMode: Boolean = false,
    val resumeDelay: Int = 2000,
)