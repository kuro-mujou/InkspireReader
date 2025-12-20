package com.inkspire.ebookreader.data.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CategoryWithBooks(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "bookId",
        associateBy = Junction(
            value = BookCategoryCrossRef::class,
            parentColumn = "categoryId",
            entityColumn = "bookId"
        )
    )
    val books: List<BookEntity>
)