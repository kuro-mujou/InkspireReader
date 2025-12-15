package com.inkspire.ebookreader.domain.model

data class Category(
    val id: Int? = null,
    val name: String,
    val color: Int,
    val isSelected: Boolean = false,
)