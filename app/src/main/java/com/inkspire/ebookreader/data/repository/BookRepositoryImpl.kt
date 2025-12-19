package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.dao.BookDao
import com.inkspire.ebookreader.data.model.BookEntity
import com.inkspire.ebookreader.domain.mapper.toDataClass
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepositoryImpl(
    private val bookDao: BookDao,
) : BookRepository {
    override suspend fun insertBook(book: BookEntity): Long {
        return bookDao.insertBook(book)
    }

    override suspend fun isBookExist(title: String): Boolean {
        return bookDao.isBookExist(title) != null
    }

    override fun getBookAsFlow(bookId: String): Flow<Book?> {
        return bookDao
            .getBookAsFlow(bookId)
            .map { it?.toDataClass() }
    }

    override fun readAllBooksSortByFavorite(): Flow<List<Book>> {
        return bookDao
            .readAllBooksSortByFavorite()
            .map { bookEntity ->
                bookEntity.map { it.toDataClass() }
            }
    }

    override suspend fun getBook(bookId: String): Book? {
        return bookDao.getBook(bookId)?.toDataClass()
    }

    override suspend fun setBookAsFavorite(bookId: String, isFavorite: Boolean) {
        bookDao.setBookAsFavorite(bookId, isFavorite)
    }

    override suspend fun updateRecentRead(bookId: String) {
        val currentRank = bookDao.getRecentRank(bookId) ?: 0

        if (currentRank == 0) {
            bookDao.shiftRanksForNew()
            bookDao.clearOldest()
        } else if (currentRank > 1) {
            bookDao.shiftRanksBefore(currentRank)
        }

        bookDao.markAsMostRecent(bookId)
    }

    override suspend fun saveBookInfoChapterIndex(bookId: String, chapterIndex: Int) {
        bookDao.saveBookInfoChapterIndex(bookId, chapterIndex)
    }

    override suspend fun saveBookInfoParagraphIndex(bookId: String, paragraphIndex: Int) {
        bookDao.saveBookInfoParagraphIndex(bookId, paragraphIndex)
    }

    override suspend fun saveBookInfoTotalChapter(bookId: String, totalChapter: Int) {
        bookDao.saveBookInfoTotalChapter(bookId, totalChapter)
    }

    override suspend fun saveBookInfoTitle(bookId: String, title: String) {
        bookDao.saveBookInfoTitle(bookId, title)
    }

    override suspend fun saveBookInfoAuthors(bookId: String, authors: List<String>) {
        bookDao.saveBookInfoAuthors(bookId, authors)
    }

    override suspend fun updateCurrentChapterIndexOnDelete(bookId: String, deleteIndex: Int) {
        bookDao.updateCurrentChapterIndexOnDelete(bookId, deleteIndex)
    }
}
