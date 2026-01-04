package com.inkspire.ebookreader.ui.bookcontent.drawer.note

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.domain.model.Note

@Immutable
data class NoteState(
    val notes: List<Note> = emptyList(),
    val undoNotes: List<Note> = emptyList(),
    val selectedNoteIndex: Int = -1,
)