package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.database.dao.NoteDao
import com.inkspire.ebookreader.data.mapper.toDataClass
import com.inkspire.ebookreader.data.mapper.toEntity
import com.inkspire.ebookreader.domain.model.Note
import com.inkspire.ebookreader.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {
    override suspend fun getNotes(bookId: String): Flow<List<Note>> {
        return noteDao.getNotes(bookId).map { noteEntityList ->
            noteEntityList.map { noteEntity ->
                noteEntity.toDataClass()
            }
        }
    }

    override suspend fun upsertNote(note: Note) {
        if (note.noteId == 0) {
            noteDao.addNote(note.toEntity())
        } else {
            noteDao.updateNoteComment(note.noteId, note.noteInput)
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        noteDao.deleteNote(noteId)
    }
}