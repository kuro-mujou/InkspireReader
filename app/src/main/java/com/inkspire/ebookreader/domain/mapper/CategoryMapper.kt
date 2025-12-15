package com.inkspire.ebookreader.domain.mapper

import com.inkspire.ebookreader.data.model.CategoryEntity
import com.inkspire.ebookreader.domain.model.Category

fun Category.toEntity(): CategoryEntity {
    return if (id == null) {
        CategoryEntity(
            name = name,
            color = color
        )
    } else {
        CategoryEntity(
            categoryId = id,
            name = name,
            color = color
        )
    }
}

fun CategoryEntity.toDataClass(): Category{
    return Category(
        id = categoryId,
        name = name,
        color = color,
    )
}