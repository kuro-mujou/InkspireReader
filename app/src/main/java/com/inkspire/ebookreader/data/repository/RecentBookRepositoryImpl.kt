package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.dao.RecentBookDao
import com.inkspire.ebookreader.domain.mapper.toDataClass
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.repository.RecentBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentBookRepositoryImpl(
    private val recentBookDao: RecentBookDao
): RecentBookRepository {
    override fun getRecentBookList(): Flow<List<Book>> {
        return recentBookDao.getTop5RecentBooksFlow().map { bookEntity ->
            bookEntity.map { it.toDataClass() }
        }
    }
}