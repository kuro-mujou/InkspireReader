package com.inkspire.ebookreader.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import com.inkspire.ebookreader.data.database.model.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Transaction
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Transaction
    @Delete
    suspend fun deleteBooks(bookEntity: BookEntity)

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE bookId = :bookId")
    suspend fun setBookAsFavorite(bookId: String, isFavorite: Boolean)

    @Query("""
        UPDATE books
        SET isRecentRead = isRecentRead - 1
        WHERE isRecentRead > :deletedRank
    """)
    suspend fun compactRanksAfterDeletion(deletedRank: Int)
}