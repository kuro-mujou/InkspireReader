package com.inkspire.ebookreader.ui.setting.music.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.domain.model.MusicItem
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import kotlinx.coroutines.launch

@Composable
fun MyMusicItemView(
    music: MusicItem,
    stylingState: StylingState?,
    onFavoriteClick: (MusicItem) -> Unit,
    onItemClick: (MusicItem) -> Unit,
    onDelete: (MusicItem) -> Unit
) {
    val configuration = LocalWindowInfo.current.containerSize
    val swipeThreshold = configuration.width / 2
    val isSelected by rememberUpdatedState(music.isSelected)
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold =  { (swipeThreshold.toFloat()) }
    )
    val scope = rememberCoroutineScope()
    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp)),
        backgroundContent = {
            if (dismissState.dismissDirection.name == SwipeToDismissBoxValue.StartToEnd.name) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = stylingState?.textBackgroundColor ?: Color.Red
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(start = 12.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
                        contentDescription = "delete",
                        tint = stylingState?.textColor ?: Color.White
                    )
                }
            }
        },
        enableDismissFromEndToStart = false,
        onDismiss = {
            if (!isSelected) {
                onDelete(music)
            } else {
                scope.launch {
                    dismissState.reset()
                }
            }
        },
        content = {
            val infiniteTransition = rememberInfiniteTransition()
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            Box(
                modifier = Modifier
                    .clickable {
                        onItemClick(music)
                    }
                    .background(
                        color = stylingState?.containerColor ?: MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(40.dp)
                            .then(
                                if (music.isSelected)
                                    Modifier.graphicsLayer(rotationZ = rotation)
                                else
                                    Modifier
                            ),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_music_disk),
                        contentDescription = null,
                        tint = if (music.isSelected)
                            stylingState?.textColor ?: Color.Red
                        else
                            stylingState?.textBackgroundColor ?: Color.Gray
                    )
                    music.name?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .weight(1f)
                                .basicMarquee(
                                    animationMode = MarqueeAnimationMode.Immediately,
                                    initialDelayMillis = 0,
                                    repeatDelayMillis = 0
                                ),
                            style = TextStyle(
                                color = stylingState?.textColor ?: Color.White,
                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex)
                            )
                        )
                    }
                    IconButton(
                        modifier = Modifier.padding(top = 4.dp, end = 4.dp),
                        onClick = {
                            onFavoriteClick(music)
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_favourite_music),
                            contentDescription = null,
                            tint = if (music.isFavorite)
                                stylingState?.textColor ?: Color.Red
                            else
                                stylingState?.textBackgroundColor ?: Color.Gray
                        )
                    }
                }
            }
        }
    )
}