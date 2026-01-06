package com.inkspire.ebookreader.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ScrapedSearchResult(
    val title: String,
    val author: String,
    val url: String,
    val coverUrl: String,
    val latestChapter: String,
    val isFull: Boolean,
    val isHot: Boolean
)
