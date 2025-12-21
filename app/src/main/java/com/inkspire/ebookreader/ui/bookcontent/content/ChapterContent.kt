package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.runtime.Composable
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
    isHighlighted: Boolean
) {
    if (linkPattern.containsMatchIn(paragraph)) {
        ImageComponent(
            stylingState = stylingState,
            uriString = paragraph,
        )
    } else if (headerPatten.containsMatchIn(paragraph)) {
        if (htmlTagPattern.replace(paragraph, replacement = "").isNotEmpty()) {
            HeaderComponent(
                stylingState = stylingState,
                text = htmlTagPattern.replace(paragraph, replacement = ""),
                isHighlighted = isHighlighted,
                textSize = calculateHeaderSize(headerLevel.find(paragraph)!!.groupValues[1].toInt(), stylingState.fontSize),
            )
        }
    } else {
        if (htmlTagPattern.replace(paragraph, replacement = "").isNotEmpty()) {
            ParagraphComponent(
                stylingState = stylingState,
                text = convertToAnnotatedStrings(paragraph),
                isHighlighted = isHighlighted,
            )
        }
    }
}