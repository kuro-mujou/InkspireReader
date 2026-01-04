package com.inkspire.ebookreader.ui.home.recentbook.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.domain.model.Book
import kotlin.math.absoluteValue

@Composable
fun MyRecentBookCard(
    book: Book,
    deviceConfiguration: DeviceConfiguration,
    pagerState: PagerState,
    pageIndex: Int,
    cardWidth: Dp? = null,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onDoubleClick = onDoubleClick
            )
            .graphicsLayer {
                val pageOffset = ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction)
                    .absoluteValue.coerceIn(0f, 1f)

                val lerpFraction = 1f - pageOffset

                val targetScale = lerp(0.85f, 1f, lerpFraction)
                val targetAlpha = lerp(0.6f, 1f, lerpFraction)
                val targetShadow = lerp(4.dp.toPx(), 12.dp.toPx(), lerpFraction)

                this.scaleX = targetScale
                this.scaleY = targetScale
                this.alpha = targetAlpha
                this.shadowElevation = targetShadow
                this.shape = cardShape
                this.clip = true
            }
            .then(
                if (cardWidth != null) {
                    Modifier.width(cardWidth)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .wrapContentHeight()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = cardShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                AsyncImage(
                    model = if (book.coverImagePath == "error") R.drawable.book_cover_not_available else book.coverImagePath,
                    contentDescription = book.title,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                )

                Box(
                    modifier = Modifier
                        .clip(cardShape)
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .align(Alignment.BottomCenter)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = book.title,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            ),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = book.authors.joinToString(","),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            progress = { (book.currentChapter + 1).toFloat() / book.totalChapter.toFloat() },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${book.currentChapter + 1} / ${book.totalChapter}",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}