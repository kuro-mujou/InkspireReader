package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import com.inkspire.ebookreader.domain.model.HighlightResult
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.bookcontent.styling.getHighlightColors
import com.inkspire.ebookreader.util.HighlightUtil

@Composable
fun HighlightListItem(
    result: HighlightResult,
    stylingState: StylingState,
    onClick: () -> Unit
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val colors = stylingState.getHighlightColors()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = result.chapterTitle,
            style = MaterialTheme.typography.labelMedium,
            color = stylingState.stylePreferences.textColor.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = AnnotatedString(result.content),
            style = TextStyle(
                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                color = stylingState.stylePreferences.textColor,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            ),
            onTextLayout = { textLayoutResult = it },
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val layout = textLayoutResult ?: return@drawBehind

                    result.highlights.forEach { highlight ->
                        val startOffset = highlight.startOffset.fastCoerceIn(0, result.content.length)
                        val endOffset = highlight.endOffset.fastCoerceIn(0, result.content.length)
                        val color = colors.getOrElse(highlight.colorIndex) { colors.first() }

                        if (startOffset < endOffset) {
                            val rects = mutableListOf<Rect>()
                            val startLine = layout.getLineForOffset(startOffset)
                            val endLine = layout.getLineForOffset(endOffset)

                            for (line in startLine..endLine) {
                                val left = if (line == startLine) layout.getHorizontalPosition(startOffset, true) else layout.getLineLeft(line)
                                val right = if (line == endLine) layout.getHorizontalPosition(endOffset, true) else layout.getLineRight(line)

                                val actualLeft = minOf(left, right)
                                val actualRight = maxOf(left, right)

                                if (actualRight - actualLeft > 1) {
                                    rects.add(
                                        Rect(
                                            left = actualLeft,
                                            top = layout.getLineTop(line),
                                            right = actualRight,
                                            bottom = layout.getLineBottom(line)
                                        )
                                    )
                                }
                            }

                            val path = HighlightUtil.createRoundedSelectionPath(rects, cornerRadius = 4.dp.toPx())
                            drawPath(path, color)
                        }
                    }
                }
        )
    }
}