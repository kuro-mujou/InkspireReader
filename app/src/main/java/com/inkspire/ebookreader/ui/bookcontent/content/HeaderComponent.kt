package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentState
import com.inkspire.ebookreader.ui.bookcontent.common.customPopupPositionProvider
import com.inkspire.ebookreader.ui.bookcontent.composable.NoteDialog
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteAction
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderComponent(
    index: Int,
    text: String,
    isHighlighted: Boolean,
    textSize: Float,
    highlightRange: () -> TextRange,
    wordOffset: () -> Int,
    stylingState: StylingState,
    chapterContentState: BookChapterContentState,
    onRequestScrollToOffset: (Float) -> Unit,
    onNoteAction: (NoteAction) -> Unit
) {
    var isOpenDialog by remember { mutableStateOf(false) }
    val paragraphBgColor = if (isHighlighted) stylingState.drawerContainerColor else Color.Transparent
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val displayedText = remember(text, isHighlighted, highlightRange()) {
        if (!isHighlighted || highlightRange().start == highlightRange().end) {
            val builder = AnnotatedString.Builder(text)
            builder.toAnnotatedString()
        } else {
            val builder = AnnotatedString.Builder(text)

            val start = highlightRange().start.fastCoerceIn(0, text.length)
            val end = highlightRange().end.fastCoerceIn(0, text.length)

            if (start < end) {
                builder.addStyle(
                    style = SpanStyle(background = stylingState.textBackgroundColor),
                    start = start,
                    end = end
                )
            }
            builder.toAnnotatedString()
        }
    }

    LaunchedEffect(wordOffset(), isHighlighted, textLayoutResult) {
        if (isHighlighted && textLayoutResult != null) {
            val layout = textLayoutResult!!
            val coercedWordOffset = { wordOffset().fastCoerceIn(0, layout.layoutInput.text.length) }
            val cursorRect = layout.getCursorRect(coercedWordOffset())
            onRequestScrollToOffset(cursorRect.bottom)
        }
    }

    val tooltipState = rememberTooltipState()
    TooltipBox(
        positionProvider = customPopupPositionProvider(),
        tooltip = {
            IconButton(
                modifier = Modifier.background(color = stylingState.textBackgroundColor, shape = CircleShape),
                onClick = {
                    isOpenDialog = true
                }
            ) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_comment), contentDescription = null)
            }
        },
        state = tooltipState,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            text = displayedText,
            onTextLayout = { textLayoutResult = it },
            style = TextStyle(
                fontSize = textSize.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                textAlign = TextAlign.Center,
                color = stylingState.textColor,
                background = paragraphBgColor,
                lineBreak = LineBreak.Paragraph,
                lineHeight = (stylingState.fontSize + stylingState.lineSpacing).sp
            )
        )
    }

    if (isOpenDialog) {
        NoteDialog(
            stylingState = stylingState,
            note = text,
            onDismiss = {
                isOpenDialog = false
            },
            onNoteChanged = { noteInput ->
                onNoteAction(
                    NoteAction.AddNote(
                        noteBody = text,
                        noteInput = noteInput,
                        tocId = chapterContentState.currentChapterIndex,
                        contentId = index
                    )
                )
            }
        )
    }
}