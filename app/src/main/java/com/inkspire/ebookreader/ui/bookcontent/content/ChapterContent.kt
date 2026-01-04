package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.runtime.Composable
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
    isHighlightedProvider: () -> Boolean,
    currentChapterIndex: () -> Int,
    onRequestScrollToOffset: (Float) -> Unit,
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
                isHighlighted = isHighlightedProvider,
                currentChapterIndex = currentChapterIndex,
                headerLevel = headerLevel.find(paragraph)!!.groupValues[1].toInt(),
                onRequestScrollToOffset = onRequestScrollToOffset,
            )
        }
    } else {
        val cleanText = htmlTagPattern.replace(paragraph, replacement = "")
        if (cleanText.isNotEmpty()) {
            ParagraphComponent(
                index = index,
                text = convertToAnnotatedStrings(paragraph),
                isHighlighted = isHighlightedProvider,
                currentChapterIndex = currentChapterIndex,
                onRequestScrollToOffset = onRequestScrollToOffset,
            )
        }
    }
}