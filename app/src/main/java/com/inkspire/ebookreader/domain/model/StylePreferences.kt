package com.inkspire.ebookreader.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class StylePreferences(
    val backgroundColor: Color = Color(0xFFF1F7ED),
    val textColor: Color = Color(0xFF1B310E),
    val selectedColorSet: Int = 0,
    val fontSize: Int = 20,
    val enableHighlight: Boolean = true,
    val textIndent: Boolean = true,
    val lineSpacing: Int = 14,
    val fontFamily: Int = 0,
    val imagePaddingState: Boolean = false,
)