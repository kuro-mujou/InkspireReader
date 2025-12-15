package com.inkspire.ebookreader.domain.model

import androidx.compose.ui.graphics.Color

data class MiniFabItem(
    val icon: Int,
    val title: String,
    val tint: Color,
    val onClick: () -> Unit
)