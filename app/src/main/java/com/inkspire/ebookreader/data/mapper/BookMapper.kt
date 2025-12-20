package com.inkspire.ebookreader.data.mapper

import com.inkspire.ebookreader.data.database.model.BookEntity
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.EmptyBook

fun Book.toEntity(): BookEntity {
    return BookEntity(
        bookId = id,
        title = title,
        coverImagePath = coverImagePath,
        authors = authors,
        description = description,
        totalChapter = totalChapter,
        currentChapter = currentChapter,
        currentParagraph = currentParagraph,
        isRecentRead = isRecentRead,
        isFavorite = isFavorite,
        storagePath = storagePath,
        isEditable = isEditable,
        fileType = fileType
    )
}

fun BookEntity.toDataClass(): Book {
    return Book(
        id = bookId,
        title = title,
        coverImagePath = coverImagePath,
        authors = authors,
        description = description,
        totalChapter = totalChapter,
        currentChapter = currentChapter,
        currentParagraph = currentParagraph,
        isRecentRead = isRecentRead,
        isFavorite = isFavorite,
        storagePath = storagePath,
        isEditable = isEditable,
        fileType = fileType
    )
}

fun EmptyBook.toDataClass(): Book {
    return Book(
        id = id ?: "",
        title = title ?: "",
        coverImagePath = coverImagePath ?: "",
        authors = authors ?: emptyList(),
        description = description,
        totalChapter = totalChapter ?: 0,
        currentChapter = currentChapter,
        currentParagraph = currentParagraph,
        isRecentRead = isRecentRead,
        isFavorite = isFavorite,
        storagePath = storagePath,
        isEditable = isEditable == true,
        fileType = fileType ?: ""
    )
}