package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface RecentBookRepository {
    fun getRecentBookList(): Flow<List<Book>>
}