package com.inkspire.ebookreader.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.inkspire.ebookreader.data.model.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentBookDao {
    @Query("SELECT * FROM books WHERE isRecentRead > 0 ORDER BY isRecentRead ASC")
    fun getTop5RecentBooksFlow(): Flow<List<BookEntity>>
}