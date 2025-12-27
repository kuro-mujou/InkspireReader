package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentState
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.headerLevel
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.headerPatten
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.htmlTagPattern
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.linkPattern
import com.inkspire.ebookreader.ui.bookcontent.common.convertToAnnotatedStrings
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteAction
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.util.HeaderTextSizeUtil.calculateHeaderSize

@Composable
fun ChapterContent(
    index: Int,
    paragraph: String,
    isHighlighted: Boolean,
    currentWordRange: TextRange,
    stylingState: StylingState,
    chapterContentState : BookChapterContentState,
    onRequestScrollToOffset: (Float) -> Unit,
    onNoteAction: (NoteAction) -> Unit
) {
    if (linkPattern.containsMatchIn(paragraph)) {
        ImageComponent(
            index = index,
            uriString = paragraph,
            stylingState = stylingState,
            chapterContentState = chapterContentState,
            onNoteAction = onNoteAction
        )
    } else if (headerPatten.containsMatchIn(paragraph)) {
        val cleanText = htmlTagPattern.replace(paragraph, replacement = "")
        if (cleanText.isNotEmpty()) {
            HeaderComponent(
                index = index,
                text = cleanText,
                stylingState = stylingState,
                isHighlighted = isHighlighted,
                chapterContentState = chapterContentState,
                currentWordRange = currentWordRange,
                textSize = calculateHeaderSize(headerLevel.find(paragraph)!!.groupValues[1].toInt(), stylingState.fontSize),
                onRequestScrollToOffset = onRequestScrollToOffset,
                onNoteAction = onNoteAction
            )
        }
    } else {
        val cleanText = htmlTagPattern.replace(paragraph, replacement = "")
        if (cleanText.isNotEmpty()) {
            ParagraphComponent(
                index = index,
                text = convertToAnnotatedStrings(paragraph),
                isHighlighted = isHighlighted,
                currentWordRange = currentWordRange,
                stylingState = stylingState,
                chapterContentState = chapterContentState,
                onRequestScrollToOffset = onRequestScrollToOffset,
                onNoteAction = onNoteAction,
            )
        }
    }
}