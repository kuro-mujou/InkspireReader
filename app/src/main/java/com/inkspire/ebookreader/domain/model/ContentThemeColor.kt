package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ContentThemeColor(
    val colorBg: Color,
    val colorTxt: Color,
)

