package com.inkspire.ebookreader.data.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.inkspire.ebookreader.data.database.converter.StringListTypeConverter

@Entity(
    tableName = "chapter_content",
    indices = [Index(
        value = ["bookId"],
        unique = false
    )],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
@TypeConverters(StringListTypeConverter::class)
data class ChapterContentEntity(
    @PrimaryKey(autoGenerate = true) val chapterContentId: Int = 0,
    val tocId: Int,
    val bookId: String,
    val chapterTitle: String,
    val content: List<String>,
)