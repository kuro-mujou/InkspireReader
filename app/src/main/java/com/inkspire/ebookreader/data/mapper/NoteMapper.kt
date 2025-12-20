package com.inkspire.ebookreader.data.mapper

import com.inkspire.ebookreader.data.database.model.NoteEntity
import com.inkspire.ebookreader.domain.model.Note

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        bookId = bookId,
        tocId = tocId,
        contentId = contentId,
        noteBody = noteBody,
        noteInput = noteInput,
        timestamp = timestamp
    )
}

fun NoteEntity.toDataClass(): Note {
    return Note(
        noteId = noteId,
        bookId = bookId,
        tocId = tocId,
        contentId = contentId,
        noteBody = noteBody,
        noteInput = noteInput,
        timestamp = timestamp
    )
}