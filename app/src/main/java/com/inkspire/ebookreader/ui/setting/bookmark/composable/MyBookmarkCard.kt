package com.inkspire.ebookreader.ui.setting.bookmark.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.BookmarkShape
import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.util.ColorUtil.darken
import com.inkspire.ebookreader.util.ColorUtil.isDark
import com.inkspire.ebookreader.util.ColorUtil.lighten
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookmarkCard(
    bookmarkContent: String,
    bookmarkIndex: Int,
    stylingState: StylingState?,
    deletable: Boolean,
    bookmarkStyle: BookmarkStyle,
    onCardClicked: () -> Unit,
    onDeleted: () -> Unit
) {
    val baseColor = if (stylingState?.containerColor?.isDark() ?: MaterialTheme.colorScheme.primary.isDark()) {
        stylingState?.containerColor?.lighten(0.2f) ?: MaterialTheme.colorScheme.primary.lighten(0.2f)
    } else {
        stylingState?.containerColor?.darken(0.2f) ?: MaterialTheme.colorScheme.primary.darken(0.2f)
    }
    val tooltipState = rememberTooltipState(
        isPersistent = true
    )
    val isBackgroundReady = remember { mutableStateOf(false) }
    var cardWidth by remember { mutableIntStateOf(0) }
    var cardHeight by remember { mutableIntStateOf(0) }
    LaunchedEffect(bookmarkIndex) {
        delay(500)
        isBackgroundReady.value = true
    }
    val cardComposable: @Composable () -> Unit = {
        ElevatedCard(
            shape = BookmarkShape(),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = stylingState?.stylePreferences?.textColor?.copy(0.3f) ?: MaterialTheme.colorScheme.primary.copy(0.3f),
                    shape = BookmarkShape()
                )
                .clickable {
                    onCardClicked()
                },
            colors = CardDefaults.elevatedCardColors(
                containerColor = stylingState?.stylePreferences?.backgroundColor ?: MaterialTheme.colorScheme.background,
                contentColor = stylingState?.stylePreferences?.textColor ?: MaterialTheme.colorScheme.primary,
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp,
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .onGloballyPositioned { coordinates ->
                            cardWidth = coordinates.size.width
                            cardHeight = coordinates.size.height
                        }
                ) {
                    AnimatedVisibility(
                        visible = isBackgroundReady.value,
                        enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 1000))
                    ) {
                        when (bookmarkStyle) {
                            BookmarkStyle.WAVE_WITH_BIRDS -> {
                                CardBackgroundWaveWithBirds(
                                    modifier = Modifier.fillMaxSize(),
                                    baseColor = baseColor,
                                    cardWidth = cardWidth,
                                    cardHeight = cardHeight
                                )
                            }

                            BookmarkStyle.CLOUD_WITH_BIRDS -> {
                                CardBackgroundCloudWithBirds(
                                    modifier = Modifier.fillMaxSize(),
                                    baseColor = baseColor,
                                    cardWidth = cardWidth,
                                    cardHeight = cardHeight
                                )
                            }

                            BookmarkStyle.STARRY_NIGHT -> {
                                CardBackgroundStarryNight(
                                    modifier = Modifier.fillMaxSize(),
                                    baseColor = baseColor,
                                    cardWidth = cardWidth,
                                    cardHeight = cardHeight
                                )
                            }

                            BookmarkStyle.GEOMETRIC_TRIANGLE -> {
                                CardBackgroundGeometricTriangle(
                                    modifier = Modifier.fillMaxSize(),
                                    baseColor = baseColor,
                                    cardWidth = cardWidth,
                                    cardHeight = cardHeight
                                )
                            }

                            BookmarkStyle.POLYGONAL_HEXAGON -> {
                                CardBackgroundPolygonalHexagon(
                                    modifier = Modifier.fillMaxSize(),
                                    baseColor = baseColor,
                                    cardWidth = cardWidth,
                                    cardHeight = cardHeight
                                )
                            }

                            BookmarkStyle.SCATTERED_HEXAGON -> {
                                CardBackgroundScatteredHexagons(
                                    modifier = Modifier.fillMaxSize(),
                                    baseColor = baseColor,
                                    cardWidth = cardWidth,
                                    cardHeight = cardHeight
                                )
                            }

                            BookmarkStyle.CHERRY_BLOSSOM_RAIN -> {
                                CardBackgroundCherryBlossomRain(
                                    modifier = Modifier.fillMaxSize(),
                                    baseColor = baseColor,
                                    cardWidth = cardWidth,
                                    cardHeight = cardHeight
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .background(
                            color = stylingState?.stylePreferences?.backgroundColor ?: MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = 4.dp,
                            end = 8.dp,
                            bottom = 4.dp
                        ),
                        text = bookmarkContent,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            color = stylingState?.stylePreferences?.textColor ?: MaterialTheme.colorScheme.primary,
                            fontFamily = stylingState?.fontFamilies[stylingState.stylePreferences.fontFamily],
                        )
                    )
                }
            }
        }
    }
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above
        ),
        tooltip = {
            IconButton(
                modifier = Modifier
                    .background(
                        color = stylingState?.textBackgroundColor ?: MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    ),
                onClick = onDeleted
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
                    contentDescription = null,
                    tint = stylingState?.stylePreferences?.textColor ?: MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        state = tooltipState,
        enableUserInput = deletable
    ) {
        cardComposable()
    }
}