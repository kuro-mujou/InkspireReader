package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.domain.model.SearchResult
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun SearchResultItem(
    result: SearchResult,
    stylingState: StylingState,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = result.chapterTitle,
            style = MaterialTheme.typography.labelMedium,
            color = stylingState.stylePreferences.textColor.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        val annotatedSnippet = buildAnnotatedString {
            var startIndex = 0

            while (startIndex < result.snippet.length) {
                val index = if (result.isCaseSensitive) {
                    result.snippet.indexOf(result.matchWord, startIndex, ignoreCase = false)
                } else {
                    result.snippet.indexOf(result.matchWord, startIndex, ignoreCase = true)
                }

                if (index == -1) {
                    append(result.snippet.substring(startIndex))
                    break
                } else {
                    append(result.snippet.substring(startIndex, index))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(result.snippet.substring(index, index + result.matchWord.length))
                    }
                    startIndex = index + result.matchWord.length
                }
            }
        }

        Text(
            text = annotatedSnippet,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                fontStyle = FontStyle.Italic
            ),
            color = stylingState.stylePreferences.textColor,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}