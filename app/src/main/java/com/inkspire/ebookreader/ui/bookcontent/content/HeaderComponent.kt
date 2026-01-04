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
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.common.LocalNoteViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTTSViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.customPopupPositionProvider
import com.inkspire.ebookreader.ui.bookcontent.composable.NoteDialog
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteAction
import com.inkspire.ebookreader.util.HeaderTextSizeUtil.calculateHeaderSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderComponent(
    index: Int,
    text: String,
    isHighlighted: () -> Boolean,
    headerLevel: Int,
    currentChapterIndex: () -> Int,
    onRequestScrollToOffset: (Float) -> Unit,
) {
    val styleVM = LocalStylingViewModel.current
    val noteVM = LocalNoteViewModel.current
    val ttsVM = LocalTTSViewModel.current

    val stylingState by styleVM.state.collectAsStateWithLifecycle()
    val highlightRange by ttsVM.currentHighlightRange.collectAsStateWithLifecycle()
    val readingOffset by ttsVM.currentReadingWordOffset.collectAsStateWithLifecycle()

    val currentHighlightRange by rememberUpdatedState(
        if (isHighlighted()) {
            highlightRange
        } else {
            TextRange.Zero
        }
    )

    val currentReadingOffset by rememberUpdatedState(
        if (isHighlighted()) {
            readingOffset
        } else {
            0
        }
    )

    val paragraphBgColor = if (isHighlighted()) stylingState.drawerContainerColor else Color.Transparent

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val displayedText = remember(text, isHighlighted(), currentHighlightRange) {
        if (!isHighlighted() || currentHighlightRange.start == currentHighlightRange.end) {
            val builder = AnnotatedString.Builder(text)
            builder.toAnnotatedString()
        } else {
            val builder = AnnotatedString.Builder(text)

            val start = currentHighlightRange.start.fastCoerceIn(0, text.length)
            val end = currentHighlightRange.end.fastCoerceIn(0, text.length)

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

    var isOpenDialog by remember { mutableStateOf(false) }
    LaunchedEffect(currentReadingOffset, isHighlighted, textLayoutResult) {
        if (isHighlighted() && textLayoutResult != null) {
            val layout = textLayoutResult!!
            val coercedWordOffset = { currentReadingOffset.fastCoerceIn(0, layout.layoutInput.text.length) }
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
                fontSize = calculateHeaderSize(headerLevel, stylingState.stylePreferences.fontSize).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                textAlign = TextAlign.Center,
                color = stylingState.stylePreferences.textColor,
                background = paragraphBgColor,
                lineBreak = LineBreak.Paragraph,
                lineHeight = (stylingState.stylePreferences.fontSize + stylingState.stylePreferences.lineSpacing).sp
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
                noteVM.onAction(
                    NoteAction.AddNote(
                        noteBody = text,
                        noteInput = noteInput,
                        tocId = currentChapterIndex(),
                        contentId = index
                    )
                )
            }
        )
    }
}