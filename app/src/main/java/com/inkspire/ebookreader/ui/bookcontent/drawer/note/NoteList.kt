package com.inkspire.ebookreader.ui.bookcontent.drawer.note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentAction
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun NoteList(
    drawerState: DrawerState,
    noteState: NoteState,
    stylingState: StylingState,
    onNoteAction: (NoteAction) -> Unit,
    onTableOfContentAction: (TableOfContentAction) -> Unit
) {
    LaunchedEffect(drawerState.visibility) {
        if (!drawerState.visibility) {
            onNoteAction(NoteAction.UnselectNote)
            onNoteAction(NoteAction.ClearUndoList)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = noteState.undoNotes.isNotEmpty()
        ) {
            IconButton(
                onClick = {
                    onNoteAction(NoteAction.UndoDeleteNote)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
                    contentDescription = null,
                    tint = stylingState.textColor
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
        ) {
            items(
                items = noteState.notes,
                key = { note -> note.noteId }
            ) { note ->
                NoteCard(
                    note = note,
                    noteState = noteState,
                    stylingState = stylingState,
                    onNoteAction = onNoteAction,
                    onTableOfContentAction = onTableOfContentAction
                )
            }
        }
    }
}