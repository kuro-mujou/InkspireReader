package com.inkspire.ebookreader.domain.model

data class FilteredResult(
    val displayText: String,
    val adjustedHighlights: List<Highlight>
)
