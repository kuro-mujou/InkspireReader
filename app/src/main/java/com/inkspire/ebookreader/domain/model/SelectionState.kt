package com.inkspire.ebookreader.domain.model

data class SelectionState(
    val isSelecting: Boolean = false,
    val startOffset: Int = 0,
    val endOffset: Int = 0
)
