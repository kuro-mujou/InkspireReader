package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun getNotes(bookId: String): Flow<List<Note>>
    suspend fun upsertNote(note: Note)
    suspend fun deleteNote(noteId: Int)
}