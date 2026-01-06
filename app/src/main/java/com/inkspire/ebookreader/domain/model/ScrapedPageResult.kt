package com.inkspire.ebookreader.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ScrapedPageResult(
    val data: List<ScrapedSearchResult>,
    val totalPages: Int
)
