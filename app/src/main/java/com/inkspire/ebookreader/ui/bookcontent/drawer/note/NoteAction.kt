package com.inkspire.ebookreader.ui.bookcontent.drawer.note

import com.inkspire.ebookreader.domain.model.Note

sealed interface NoteAction {
    data class AddNote(val noteBody: String, val noteInput: String, val tocId: Int, val contentId: Int) : NoteAction
    data class DeleteNote(val note: Note) : NoteAction
    data class EditNote(val note: Note, val newNoteBody: String) : NoteAction
    data object UndoDeleteNote : NoteAction
    data class SelectNote(val note: Note) : NoteAction
    data object UnselectNote : NoteAction
    data object ClearUndoList : NoteAction
}