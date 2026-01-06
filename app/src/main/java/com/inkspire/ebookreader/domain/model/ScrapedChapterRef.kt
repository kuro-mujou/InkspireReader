package com.inkspire.ebookreader.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ScrapedChapterRef(
    val title: String,
    val url: String,
    val index: Int
)