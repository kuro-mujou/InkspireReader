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
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentAction
import com.inkspire.ebookreader.ui.bookcontent.common.customPopupPositionProvider
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphComponent(
    stylingState: StylingState,
    isHighlighted: Boolean,
    text: AnnotatedString,
    currentWordRange: TextRange,
    onRequestScrollToOffset: (Float) -> Unit,
    onContentAction: (BookChapterContentAction) -> Unit
) {
    val paragraphBgColor = if (isHighlighted) stylingState.textBackgroundColor else Color.Transparent
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val displayedText = remember(text, isHighlighted, currentWordRange) {
        if (!isHighlighted || currentWordRange.start == currentWordRange.end) {
            text
        } else {
            val builder = AnnotatedString.Builder(text)

            val start = currentWordRange.start.coerceIn(0, text.length)
            val end = currentWordRange.end.coerceIn(0, text.length)

            if (start < end) {
                builder.addStyle(
                    style = SpanStyle(background = stylingState.wordHighlightColor),
                    start = start,
                    end = end
                )
            }
            builder.toAnnotatedString()
        }
    }

    LaunchedEffect(currentWordRange, isHighlighted, textLayoutResult) {
        if (isHighlighted && textLayoutResult != null) {
            val layout = textLayoutResult!!
            val safeOffset = currentWordRange.start.coerceIn(0, layout.layoutInput.text.length)
            val cursorRect = layout.getCursorRect(safeOffset)
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
                    onContentAction(BookChapterContentAction.ChangeNoteDialogVisibility)
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
                textIndent = if (stylingState.textIndent) TextIndent(firstLine = (stylingState.fontSize * 2).sp) else TextIndent.None,
                fontSize = stylingState.fontSize.sp,
                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                textAlign = if (stylingState.textAlign) TextAlign.Justify else TextAlign.Left,
                color = stylingState.textColor,
                background = paragraphBgColor,
                lineBreak = LineBreak.Paragraph,
                lineHeight = (stylingState.fontSize + stylingState.lineSpacing).sp
            )
        )
    }
}