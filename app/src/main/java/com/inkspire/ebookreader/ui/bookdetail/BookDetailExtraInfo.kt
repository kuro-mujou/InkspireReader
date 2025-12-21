package com.inkspire.ebookreader.ui.bookdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.common.ContentPattern
import com.inkspire.ebookreader.ui.composable.MyBookChip

@Composable
fun BookDetailExtraInfo(
    modifier: Modifier,
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Category",
                modifier = Modifier
                    .fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Medium
                )
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                onClick = {
                    onAction(BookDetailAction.ChangeCategoryMenuVisibility)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_add_music),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        if (state.bookWithCategories?.categories?.isNotEmpty() == true) {
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.bookWithCategories.categories.forEach {
                    MyBookChip(
                        selected = false,
                        onClick = {},
                        color = Color(it.color)
                    ) {
                        Text(text = it.name)
                    }
                }
            }
        } else {
            Text(
                text = "no category available",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle(
                    textIndent = TextIndent(firstLine = 20.sp),
                    textAlign = TextAlign.Justify,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            )
        }
        Text(
            text = "Description",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = state.bookWithCategories?.book?.description?.let {
                ContentPattern.htmlTagPattern.replace(it, replacement = "")
            } ?: "no description available",
            modifier = Modifier.padding(
                top = 4.dp,
                bottom = 4.dp,
                start = 8.dp,
                end = 8.dp
            ),
            style = TextStyle(
                textIndent = TextIndent(firstLine = 20.sp),
                textAlign = TextAlign.Justify,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        )
    }
}