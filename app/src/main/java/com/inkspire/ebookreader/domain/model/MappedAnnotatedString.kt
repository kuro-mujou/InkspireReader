package com.inkspire.ebookreader.domain.model

import androidx.compose.ui.text.AnnotatedString

data class MappedAnnotatedString(
    val text: AnnotatedString,
    val displayToRawMap: List<Int>
)