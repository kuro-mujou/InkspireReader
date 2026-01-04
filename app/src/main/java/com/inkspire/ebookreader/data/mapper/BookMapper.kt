package com.inkspire.ebookreader.data.mapper

import com.inkspire.ebookreader.data.database.model.BookEntity
import com.inkspire.ebookreader.data.database.model.BookWithCategories
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.BookWithCategoriesModel
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

fun BookWithCategories.toDataClass(): BookWithCategoriesModel {
    return BookWithCategoriesModel(
        id = book.bookId,
        title = book.title,
        coverImagePath = book.coverImagePath,
        authors = book.authors,
        description = book.description,
        totalChapter = book.totalChapter,
        currentChapter = book.currentChapter,
        currentParagraph = book.currentParagraph,
        isRecentRead = book.isRecentRead,
        isFavorite = book.isFavorite,
        storagePath = book.storagePath,
        isEditable = book.isEditable,
        fileType = book.fileType,
        categories = categories.map { it.toDataClass() }
    )
}

fun BookWithCategoriesModel.toNormalBook(): Book {
    return Book(
        id = id,
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