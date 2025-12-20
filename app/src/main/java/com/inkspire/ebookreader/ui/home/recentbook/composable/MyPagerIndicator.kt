package com.inkspire.ebookreader.ui.home.recentbook.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun MyPagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = activeColor.copy(alpha = 0.3f),
) {
    val pageCount = pagerState.pageCount
    val dotSize = 8.dp
    val spacing = 6.dp

    Spacer(
        modifier = modifier
            .size(
                width = (dotSize * pageCount) + (spacing * (pageCount - 1).coerceAtLeast(0)),
                height = dotSize * 1.2f
            )
            .drawBehind {
                val baseRadius = dotSize.toPx() / 2f
                val spacingPx = spacing.toPx()

                val currentPage = pagerState.currentPage
                val targetPage = pagerState.targetPage
                val offsetFraction = pagerState.currentPageOffsetFraction.absoluteValue

                repeat(pageCount) { index ->
                    val color = when (index) {
                        currentPage -> lerp(activeColor, inactiveColor, offsetFraction)
                        targetPage -> lerp(inactiveColor, activeColor, offsetFraction)
                        else -> inactiveColor
                    }

                    val scale = if (index == currentPage || index == targetPage) {
                        val fraction = if (index == currentPage) 1f - offsetFraction else offsetFraction
                        1f + (0.2f * fraction)
                    } else {
                        1f
                    }

                    val xCenter = baseRadius + (index * (dotSize.toPx() + spacingPx))
                    val yCenter = size.height / 2

                    drawCircle(
                        color = color,
                        radius = baseRadius * scale,
                        center = Offset(xCenter, yCenter)
                    )
                }
            }
    )
}