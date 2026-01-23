package com.inkspire.ebookreader.data.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "highlights",
    indices = [
        Index(value = ["bookId"]),
        Index(value = ["tocId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true) val highlightId: Long = 0,
    val bookId: String,
    val tocId: Int,
    val paragraphIndex: Int,
    val startOffset: Int,
    val endOffset: Int,
    val colorIndex: Int,
    val createdTime: Long = System.currentTimeMillis(),
    val note: String? = null
)