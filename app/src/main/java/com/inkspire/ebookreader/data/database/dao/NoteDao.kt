package com.inkspire.ebookreader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.inkspire.ebookreader.data.database.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Transaction
    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY tocId ASC, contentId ASC, timestamp DESC")
    fun getNotes(bookId: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: NoteEntity)

    @Query("UPDATE notes SET noteInput = :noteInput WHERE noteId = :noteId")
    suspend fun updateNoteComment(noteId: Int, noteInput: String)

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun deleteNote(noteId: Int)
}