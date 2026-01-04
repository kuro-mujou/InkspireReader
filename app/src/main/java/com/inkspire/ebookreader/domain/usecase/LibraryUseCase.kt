package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ImagePathRepository

class LibraryUseCase(
    private val bookRepository: BookRepository,
    private val imagePathRepository: ImagePathRepository
) {
    fun getBookCategory() = bookRepository.getBookCategory()
    fun getAllBooksWithCategories() = bookRepository.getAllBooksWithCategories()
    suspend fun setBookAsFavorite(bookId: String, isFavorite: Boolean) = bookRepository.setBookAsFavorite(bookId, isFavorite)
    suspend fun deleteBooks(books: List<Book>) = bookRepository.deleteBooks(books)
    suspend fun getImagePathsByBookIds(bookId: List<String>) = imagePathRepository.getImagePathsByBookIds(bookId)
    suspend fun deleteByBookIds(bookId: List<String>) = imagePathRepository.deleteByBookIds(bookId)
    suspend fun updateRecentRead(bookId: String) = bookRepository.updateRecentRead(bookId)
}