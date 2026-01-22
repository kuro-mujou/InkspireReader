package com.inkspire.ebookreader.domain.model

data class HiddenText(
    val id: Int,
    val textToHide: String,
    val isSelected: Boolean = false
)
