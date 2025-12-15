package com.inkspire.ebookreader.domain.model

data class MusicItem(
    val id: Int? = null,
    val name: String? = null,
    val uri: String? = null,
    val isFavorite: Boolean = false,
    val isSelected: Boolean = false
)