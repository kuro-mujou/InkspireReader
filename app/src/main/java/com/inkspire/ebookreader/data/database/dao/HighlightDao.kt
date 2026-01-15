package com.inkspire.ebookreader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inkspire.ebookreader.data.database.model.HighlightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HighlightDao {

    @Query("SELECT * FROM highlights WHERE bookId = :bookId AND tocId = :tocId")
    fun getHighlightsForChapter(bookId: String, tocId: Int): Flow<List<HighlightEntity>>

    @Query("SELECT * FROM highlights WHERE bookId = :bookId ORDER BY createdTime DESC")
    fun getAllHighlightsForBook(bookId: String): Flow<List<HighlightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: HighlightEntity)

    @Query("DELETE FROM highlights WHERE bookId = :bookId AND tocId = :tocId AND paragraphIndex = :paragraphIndex")
    suspend fun deleteAllForParagraph(bookId: String, tocId: Int, paragraphIndex: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlights(highlights: List<HighlightEntity>)

    @Query("SELECT * FROM highlights WHERE bookId = :bookId AND tocId = :tocId AND paragraphIndex = :paragraphIndex")
    suspend fun getHighlightsForParagraphSync(bookId: String, tocId: Int, paragraphIndex: Int): List<HighlightEntity>
}