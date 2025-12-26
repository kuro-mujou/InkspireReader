package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentAction
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.headerLevel
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.headerPatten
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.htmlTagPattern
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.linkPattern
import com.inkspire.ebookreader.ui.bookcontent.common.convertToAnnotatedStrings
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.util.HeaderTextSizeUtil.calculateHeaderSize

@Composable
fun ChapterContent(
    stylingState: StylingState,
    paragraph: String,
    isHighlighted: Boolean,
    currentWordRange: TextRange,
    onRequestScrollToOffset: (Float) -> Unit,
    onContentAction: (BookChapterContentAction) -> Unit
) {
    if (linkPattern.containsMatchIn(paragraph)) {
        ImageComponent(
            stylingState = stylingState,
            uriString = paragraph,
        )
    } else if (headerPatten.containsMatchIn(paragraph)) {
        val cleanText = htmlTagPattern.replace(paragraph, replacement = "")
        if (cleanText.isNotEmpty()) {
            HeaderComponent(
                stylingState = stylingState,
                text = cleanText,
                isHighlighted = isHighlighted,
                textSize = calculateHeaderSize(headerLevel.find(paragraph)!!.groupValues[1].toInt(), stylingState.fontSize),
                currentWordRange = currentWordRange,
                onRequestScrollToOffset = onRequestScrollToOffset,
                onContentAction = onContentAction
            )
        }
    } else {
        val cleanText = htmlTagPattern.replace(paragraph, replacement = "")
        if (cleanText.isNotEmpty()) {
            ParagraphComponent(
                stylingState = stylingState,
                text = convertToAnnotatedStrings(paragraph),
                isHighlighted = isHighlighted,
                currentWordRange = currentWordRange,
                onRequestScrollToOffset = onRequestScrollToOffset,
                onContentAction = onContentAction
            )
        }
    }
}