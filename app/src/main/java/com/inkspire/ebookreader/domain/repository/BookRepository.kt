package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.data.model.BookEntity
import com.inkspire.ebookreader.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun readAllBooksSortByFavorite(): Flow<List<Book>>
    fun getBookAsFlow(bookId: String): Flow<Book?>
    suspend fun getBook(bookId: String): Book?
    suspend fun insertBook(book: BookEntity): Long
    suspend fun isBookExist(title: String): Boolean
    suspend fun setBookAsFavorite(bookId: String, isFavorite: Boolean)
    suspend fun updateRecentRead(bookId: String)
    suspend fun saveBookInfoChapterIndex(bookId: String, chapterIndex: Int)
    suspend fun saveBookInfoParagraphIndex(bookId: String, paragraphIndex: Int)
    suspend fun saveBookInfoTotalChapter(bookId: String, totalChapter: Int)
    suspend fun saveBookInfoTitle(bookId: String, title: String)
    suspend fun saveBookInfoAuthors(bookId: String, authors: List<String>)
    suspend fun updateCurrentChapterIndexOnDelete(bookId: String, deleteIndex: Int)
}