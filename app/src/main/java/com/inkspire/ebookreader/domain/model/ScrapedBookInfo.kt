package com.inkspire.ebookreader.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ScrapedBookInfo(
    val title: String,
    val author: String,
    val descriptionHtml: String,
    val coverUrl: String,
    val categories: List<String> = emptyList(),
    val status: String = "Unknown"
)
