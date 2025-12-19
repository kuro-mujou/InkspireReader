package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getAllBooksFlow(): Flow<List<Book>>
    suspend fun deleteBooks(books: List<Book>)
    suspend fun setBookAsFavorite(bookId: String, isFavorite: Boolean)
}