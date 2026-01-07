package com.inkspire.ebookreader.ui.home.explore.detail.composable

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkspire.ebookreader.domain.model.ScrapedBookInfo

@Composable
fun DetailFooter(
    modifier: Modifier,
    searchedBooks: ScrapedBookInfo,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "Description:",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Html.fromHtml(searchedBooks.descriptionHtml, Html.FROM_HTML_MODE_COMPACT).toString().trim(),
                style = TextStyle(
                    textIndent = TextIndent(firstLine = 16.sp),
                    textAlign = TextAlign.Justify,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "Categories:",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = searchedBooks.categories.joinToString(", ").trim(),
                style = TextStyle(
                    textAlign = TextAlign.Justify,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            )
        }
    }
}