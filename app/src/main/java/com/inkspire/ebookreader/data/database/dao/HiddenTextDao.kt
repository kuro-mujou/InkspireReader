package com.inkspire.ebookreader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inkspire.ebookreader.data.database.model.HiddenTextEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenTextDao {
    @Query("SELECT * FROM hidden_texts")
    fun getHiddenTexts(): Flow<List<HiddenTextEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHiddenText(regex: HiddenTextEntity)

    @Query("DELETE FROM hidden_texts WHERE id IN (:ids)")
    suspend fun deleteHiddenTexts(ids: List<Int>)
}