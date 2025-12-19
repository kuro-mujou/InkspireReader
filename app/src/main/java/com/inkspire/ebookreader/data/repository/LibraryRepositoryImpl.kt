package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.dao.LibraryDao
import com.inkspire.ebookreader.domain.mapper.toDataClass
import com.inkspire.ebookreader.domain.mapper.toEntity
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryRepositoryImpl(
    private val libraryDao: LibraryDao
): LibraryRepository {
    override fun getAllBooksFlow(): Flow<List<Book>> {
        return libraryDao
            .getAllBooks()
            .map { bookEntity ->
                bookEntity.map { it.toDataClass() }
            }
    }

    override suspend fun deleteBooks(books: List<Book>) {
        val bookEntities = books.map { it.toEntity() }

        val deletedRanks = bookEntities
            .mapNotNull { entity ->
                if (entity.isRecentRead > 0) entity.isRecentRead else null
            }
            .distinct()
            .sorted()

        bookEntities.forEach { libraryDao.deleteBooks(it) }
        deletedRanks.forEach { rank ->
            libraryDao.compactRanksAfterDeletion(rank)
        }
    }

    override suspend fun setBookAsFavorite(bookId: String, isFavorite: Boolean) {
        libraryDao.setBookAsFavorite(bookId, isFavorite)
    }
}