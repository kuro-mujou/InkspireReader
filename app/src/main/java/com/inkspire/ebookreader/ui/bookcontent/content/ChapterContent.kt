package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import com.inkspire.ebookreader.domain.model.Highlight
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.headerLevel
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.headerPatten
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.htmlTagPattern
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.linkPattern
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern.linkPatternDebug
import com.inkspire.ebookreader.ui.bookcontent.common.convertToAnnotatedStrings

@Composable
fun ChapterContent(
    index: Int,
    paragraph: String,
    isTTSHighlightProvider: () -> Boolean,
    highlights: () -> List<Highlight>,
    currentChapterIndex: () -> Int,
    onRequestScrollToOffset: (Float) -> Unit,
    onMagnifierChange: (Offset) -> Unit
) {
    if (linkPattern.containsMatchIn(paragraph) || linkPatternDebug.containsMatchIn(paragraph)) {
        ImageComponent(
            index = index,
            uriString = paragraph,
            currentChapterIndex = currentChapterIndex,
        )
    } else if (headerPatten.containsMatchIn(paragraph)) {
        val cleanText = htmlTagPattern.replace(paragraph, replacement = "")
        if (cleanText.isNotEmpty()) {
            HeaderComponent(
                index = index,
                text = cleanText,
                isTTSHighlighted = isTTSHighlightProvider,
                currentChapterIndex = currentChapterIndex,
                headerLevel = headerLevel.find(paragraph)!!.groupValues[1].toInt(),
                onRequestScrollToOffset = onRequestScrollToOffset,
                highlights = highlights,
                onMagnifierChange = onMagnifierChange
            )
        }
    } else {
        val cleanText = htmlTagPattern.replace(paragraph, replacement = "")
        if (cleanText.isNotEmpty()) {
            ParagraphComponent(
                index = index,
                text = convertToAnnotatedStrings(paragraph),
                isTTSHighlighted = isTTSHighlightProvider,
                highlights = highlights,
                currentChapterIndex = currentChapterIndex,
                onRequestScrollToOffset = onRequestScrollToOffset,
                onMagnifierChange = onMagnifierChange
            )
        }
    }
}