package com.inkspire.ebookreader.data.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class BookWithCategories(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "categoryId",
        associateBy = Junction(
            value = BookCategoryCrossRef::class,
        )
    )
    val categories: List<CategoryEntity>
)