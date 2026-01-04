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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.common.LocalCombineActions
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalNoteViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel

@Composable
fun NoteList() {
    val combineActions = LocalCombineActions.current
    val drawerVM = LocalDrawerViewModel.current
    val stylingVM = LocalStylingViewModel.current
    val noteVM = LocalNoteViewModel.current

    val drawerState by drawerVM.state.collectAsStateWithLifecycle()
    val stylingState by stylingVM.state.collectAsStateWithLifecycle()
    val noteState by noteVM.state.collectAsStateWithLifecycle()

    LaunchedEffect(drawerState.visibility) {
        if (!drawerState.visibility) {
            noteVM.onAction(NoteAction.UnselectNote)
            noteVM.onAction(NoteAction.ClearUndoList)
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
                    noteVM.onAction(NoteAction.UndoDeleteNote)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
                    contentDescription = null,
                    tint = stylingState.stylePreferences.textColor
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
                    onNoteAction = noteVM::onAction,
                    onCardClicked = { tocId, contentId ->
                        combineActions.navigateToParagraph(tocId, contentId)
                    }
                )
            }
        }
    }
}