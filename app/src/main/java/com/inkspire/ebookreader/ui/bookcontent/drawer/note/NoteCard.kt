package com.inkspire.ebookreader.ui.bookcontent.drawer.note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.domain.model.Note
import com.inkspire.ebookreader.ui.bookcontent.composable.NoteDialog
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun NoteCard(
    note: Note,
    noteState: NoteState,
    stylingState: StylingState,
    onNoteAction: (NoteAction) -> Unit,
    onCardClicked: (Int, Int) -> Unit,
) {
    var isOpenDialog by remember { mutableStateOf(false) }
    if (isOpenDialog) {
        NoteDialog(
            note = note.noteBody,
            noteInput = note.noteInput,
            stylingState = stylingState,
            onDismiss = {
                isOpenDialog = false
            },
            onNoteChanged = {
                onNoteAction(NoteAction.EditNote(note, it))
            }
        )
    }
    ElevatedCard(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onCardClicked(note.tocId, note.contentId)
                },
                onLongClick = {
                    onNoteAction(NoteAction.SelectNote(note))
                }
            )
            .then(
                if (noteState.selectedNoteIndex == note.noteId) {
                    Modifier.border(
                        width = 2.dp,
                        color = stylingState.stylePreferences.textColor,
                        shape = CardDefaults.elevatedShape
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = stylingState.stylePreferences.backgroundColor,
            contentColor = stylingState.stylePreferences.textColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
            ) {
                VerticalDivider(
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    thickness = 2.dp,
                    color = stylingState.stylePreferences.textColor
                )
                Text(
                    text = note.noteBody,
                    modifier = Modifier.padding(all = 8.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontStyle = FontStyle.Italic,
                        color = stylingState.stylePreferences.textColor,
                        textAlign = TextAlign.Justify,
                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                    )
                )
            }
            Text(
                text = note.noteInput,
                style = TextStyle(
                    color = stylingState.stylePreferences.textColor,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Justify,
                    fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                )
            )
            Text(
                text = note.timestamp,
                modifier = Modifier.align(Alignment.End),
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    color = stylingState.stylePreferences.textColor,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Justify,
                    fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                )
            )
            AnimatedVisibility(
                visible = noteState.selectedNoteIndex == note.noteId
            ) {
                Row {
                    IconButton(
                        onClick = {
                            onNoteAction(NoteAction.DeleteNote(note))
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
                            contentDescription = "Delete",
                            tint = stylingState.stylePreferences.textColor
                        )
                    }
                    IconButton(
                        onClick = {
                            isOpenDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                            contentDescription = "Edit",
                            tint = stylingState.stylePreferences.textColor
                        )
                    }
                }
            }
        }
    }
}