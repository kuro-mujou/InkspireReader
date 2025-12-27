package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentState
import com.inkspire.ebookreader.ui.bookcontent.common.customPopupPositionProvider
import com.inkspire.ebookreader.ui.bookcontent.composable.NoteDialog
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteAction
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageComponent(
    index: Int,
    uriString: String,
    stylingState: StylingState,
    chapterContentState: BookChapterContentState,
    onNoteAction: (NoteAction) -> Unit
) {
    var isOpenDialog by remember { mutableStateOf(false) }
    val tooltipState = rememberTooltipState()
    TooltipBox(
        positionProvider = customPopupPositionProvider(),
        tooltip = {
            IconButton(
                modifier = Modifier.background(color = stylingState.textBackgroundColor, shape = CircleShape),
                onClick = {
                    isOpenDialog = true
                },
            ) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_comment), contentDescription = null)
            }
        },
        state = tooltipState,
    ) {
        Card(
            modifier = Modifier
                .then(
                    if (stylingState.imagePaddingState)
                        Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    else
                        Modifier
                )
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RectangleShape
        ) {
            AsyncImage(
                model = uriString,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

    if (isOpenDialog) {
        NoteDialog(
            stylingState = stylingState,
            note = uriString,
            onDismiss = {
                isOpenDialog = false
            },
            onNoteChanged = { noteInput ->
                onNoteAction(
                    NoteAction.AddNote(
                        noteBody = uriString,
                        noteInput = noteInput,
                        tocId = chapterContentState.currentChapterIndex,
                        contentId = index
                    )
                )
            }
        )
    }
}