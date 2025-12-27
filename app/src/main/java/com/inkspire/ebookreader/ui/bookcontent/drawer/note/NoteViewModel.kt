package com.inkspire.ebookreader.ui.bookcontent.drawer.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.model.Note
import com.inkspire.ebookreader.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NoteViewModel(
    private val bookId: String,
    private val noteRepository: NoteRepository
): ViewModel() {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm  dd-MM-yyyy")
    private val _state = MutableStateFlow(NoteState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    init {
        viewModelScope.launch {
            noteRepository.getNotes(bookId).collectLatest { notes ->
                _state.update { it.copy(notes = notes) }
            }
        }
    }

    fun onAction(action: NoteAction) {
        when(action) {
            is NoteAction.AddNote -> {
                viewModelScope.launch {
                    noteRepository.upsertNote(
                        Note(
                            bookId = bookId,
                            tocId = action.tocId,
                            contentId = action.contentId,
                            noteBody = action.noteBody,
                            noteInput = action.noteInput,
                            timestamp = LocalDateTime.now().format(dateTimeFormatter)
                        )
                    )
                }
            }

            is NoteAction.EditNote -> {
                viewModelScope.launch {
                    noteRepository.upsertNote(
                        Note(
                            noteId = action.note.noteId,
                            bookId = action.note.bookId,
                            tocId = action.note.tocId,
                            contentId = action.note.contentId,
                            noteBody = action.note.noteBody,
                            noteInput = action.newNoteBody,
                            timestamp = LocalDateTime.now().format(dateTimeFormatter)
                        )
                    )
                }
            }

            is NoteAction.DeleteNote -> {
                viewModelScope.launch {
                    noteRepository.deleteNote(action.note.noteId)
                }
                _state.update {
                    it.copy(
                        undoNotes = _state.value.undoNotes + action.note,
                        selectedNoteIndex = -1
                    )
                }
            }

            is NoteAction.UndoDeleteNote -> {
                viewModelScope.launch {
                    _state.value.undoNotes.forEach {
                        noteRepository.upsertNote(it)
                    }
                    _state.update { it.copy(undoNotes = emptyList()) }
                }
            }

            is NoteAction.SelectNote -> {
                _state.update { it.copy(selectedNoteIndex = action.note.noteId) }
            }

            is NoteAction.UnselectNote -> {
                _state.update { it.copy(selectedNoteIndex = -1) }
            }

            is NoteAction.ClearUndoList -> {
                _state.update { it.copy(undoNotes = emptyList()) }
            }
        }
    }
}