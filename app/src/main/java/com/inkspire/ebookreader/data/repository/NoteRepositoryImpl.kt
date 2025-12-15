package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.dao.NoteDao
import com.inkspire.ebookreader.domain.mapper.toDataClass
import com.inkspire.ebookreader.domain.mapper.toEntity
import com.inkspire.ebookreader.domain.model.Note
import com.inkspire.ebookreader.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {
    override suspend fun getNotes(bookId: String): Flow<List<Note>> {
        return noteDao.getNotes(bookId).map { noteEntityList ->
            noteEntityList.reversed().map { noteEntity ->
                noteEntity.toDataClass()
            }
        }
    }

    override suspend fun upsertNote(note: Note) {
        noteDao.upsertBasedOnIds(note.toEntity())
    }

    override suspend fun deleteNote(noteId: Int) {
        noteDao.deleteNote(noteId)
    }
}