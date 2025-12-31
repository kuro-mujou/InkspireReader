package com.inkspire.ebookreader.ui.home.explore.detail.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.common.ScrapedBookInfo
import com.inkspire.ebookreader.ui.home.explore.detail.DetailAction

@Composable
fun DetailHeader(
    deviceConfiguration: DeviceConfiguration,
    onAction: (DetailAction) -> Unit,
    searchedBooks: ScrapedBookInfo,
    chapterInfo: String
) {
    when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT,
        DeviceConfiguration.TABLET_PORTRAIT -> {
            IconButton(
                onClick = {
                    onAction(DetailAction.NavigateBack)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow),
                    contentDescription = "Back"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
            ) {
                AsyncImage(
                    model = searchedBooks.coverUrl,
                    contentDescription = searchedBooks.title,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = searchedBooks.title.trim(),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = searchedBooks.author.trim(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = chapterInfo,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                            )
                        )
                        Text(
                            text = "Status: " + searchedBooks.status,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                            )
                        )
                    }
                }
            }
        }
        DeviceConfiguration.PHONE_LANDSCAPE,
        DeviceConfiguration.TABLET_LANDSCAPE -> {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(200.dp)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            onAction(DetailAction.NavigateBack)
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow),
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    AsyncImage(
                        model = searchedBooks.coverUrl,
                        contentDescription = searchedBooks.title,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .width(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = searchedBooks.title.trim(),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            textAlign = TextAlign.End,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = searchedBooks.author.trim(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            textAlign = TextAlign.End,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    )
                    Text(
                        text = chapterInfo,
                        style = TextStyle(
                            textAlign = TextAlign.End,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    )
                    Text(
                        text = "Status: " + searchedBooks.status,
                        style = TextStyle(
                            textAlign = TextAlign.End,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(4.dp),
                    onClick = {
                        onAction(DetailAction.DownloadBook(searchedBooks))
                    }
                ) {
                    Text("Download")
                }
            }
        }
    }
}