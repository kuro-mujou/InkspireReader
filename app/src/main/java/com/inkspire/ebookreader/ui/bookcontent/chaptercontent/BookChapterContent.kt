package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollAction
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollState
import com.inkspire.ebookreader.ui.bookcontent.content.ChapterContent
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataAction
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSPlaybackState
import com.inkspire.ebookreader.ui.composable.MyLoadingAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun BookChapterContent(
    bookInfo: Book,
    initialParagraphIndex: Int,
    currentChapter: Int,
    stylingState: StylingState,
    bookChapterContentState: BookChapterContentState,
    chapterUiState: UiState<Chapter>,
    ttsPlaybackState: TTSPlaybackState,
    autoScrollState: AutoScrollState,
    isCurrentChapter: Boolean,
    onBookContentDataAction: (BookContentDataAction) -> Unit,
    onBookChapterContentAction: (BookChapterContentAction) -> Unit,
    onAutoScrollAction: (AutoScrollAction) -> Unit,
    onListStateLoaded: (LazyListState) -> Unit,
    onDispose: () -> Unit
) {
    var originalOffset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var originalZoom by remember { mutableFloatStateOf(1f) }
    val density = LocalDensity.current

    LaunchedEffect(currentChapter) {
        onBookContentDataAction(BookContentDataAction.LoadChapter(currentChapter))
    }

    when (chapterUiState) {
        UiState.None -> {

        }
        is UiState.Loading -> {
            MyLoadingAnimation()
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${chapterUiState.throwable.message}")
            }
        }
        is UiState.Success -> {
            val chapterData = chapterUiState.data
            val paragraphs = chapterData.content
            val listState = rememberLazyListState(
                initialFirstVisibleItemIndex = initialParagraphIndex
            )

            val currentAutoScrollState by rememberUpdatedState(autoScrollState)
            val currentAutoScrollAction by rememberUpdatedState(onAutoScrollAction)
            val isAtBottom by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val visibleItemsInfo = layoutInfo.visibleItemsInfo
                    if (layoutInfo.totalItemsCount == 0) {
                        false
                    } else {
                        val lastVisibleItem = visibleItemsInfo.lastOrNull()
                        lastVisibleItem?.index == layoutInfo.totalItemsCount - 1 &&
                                (lastVisibleItem.offset + lastVisibleItem.size) <= layoutInfo.viewportEndOffset
                    }
                }
            }

            DisposableEffect(listState) {
                onListStateLoaded(listState)
                onDispose { onDispose() }
            }

            LaunchedEffect(listState, isCurrentChapter) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index }
                    .collect { index ->
                        if (isCurrentChapter && index != null) {
                            onBookContentDataAction(BookContentDataAction.UpdateRecentParagraphToDB(index))
                            onBookChapterContentAction(BookChapterContentAction.UpdateFirstVisibleItemIndex(index))
                        }
                    }
            }
            LaunchedEffect(listState, isCurrentChapter) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { index ->
                        if (isCurrentChapter && index != null) {
                            onBookChapterContentAction(BookChapterContentAction.UpdateLastVisibleItemIndex(index))
                        }
                    }
            }
            LaunchedEffect(originalZoom) {
                if (originalZoom > 1f) {
                    onBookChapterContentAction(BookChapterContentAction.UpdateEnablePagerScroll(false))
                    onBookChapterContentAction(BookChapterContentAction.UpdateEnableUndoButton(true))
                } else {
                    onBookChapterContentAction(BookChapterContentAction.UpdateEnablePagerScroll(true))
                    onBookChapterContentAction(BookChapterContentAction.UpdateEnableUndoButton(false))
                }
            }

            LaunchedEffect(bookChapterContentState.enableUndoButton) {
                if (!bookChapterContentState.enableUndoButton) {
                    originalZoom = 1f
                    originalOffset = Offset.Zero
                }
            }

            LaunchedEffect(
                listState.interactionSource,
                currentAutoScrollState.autoScrollResumeDelayTime
            ) {
                listState.interactionSource.interactions.collect { interaction ->
                    if (!currentAutoScrollState.isActivated) return@collect

                    when (interaction) {
                        is PressInteraction.Press, is DragInteraction.Start -> {
                            currentAutoScrollAction(AutoScrollAction.UpdateIsPaused(true))
                        }
                        is PressInteraction.Release, is DragInteraction.Stop, is DragInteraction.Cancel -> {
                            if (currentAutoScrollState.autoScrollResumeMode) {
                                delay(currentAutoScrollState.autoScrollResumeDelayTime.toLong())
                                currentAutoScrollAction(AutoScrollAction.UpdateIsPaused(false))
                            }
                        }
                    }
                }
            }

            LaunchedEffect(
                currentChapter,
                isCurrentChapter,
                currentAutoScrollState.delayTimeAtStart,
            ) {
                if (isCurrentChapter && autoScrollState.isActivated) {
                    if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
                        onAutoScrollAction(AutoScrollAction.UpdateIsPaused(true))
                        delay(autoScrollState.delayTimeAtStart.toLong())
                        onAutoScrollAction(AutoScrollAction.UpdateIsPaused(false))
                    }
                }
            }

            LaunchedEffect(
                currentAutoScrollState.isActivated,
                currentAutoScrollState.isPaused,
                currentAutoScrollState.autoScrollSpeed,
                currentAutoScrollState.delayTimeAtEnd,
                isCurrentChapter,
                currentChapter
            ) {
                if (isCurrentChapter && currentAutoScrollState.isActivated && !currentAutoScrollState.isPaused) {
                    if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
                        delay(currentAutoScrollState.delayTimeAtStart.toLong())
                        currentAutoScrollAction(AutoScrollAction.UpdateIsPaused(false))
                    }
                    while (isActive) {
                        if (isAtBottom) {
                            onAutoScrollAction(AutoScrollAction.UpdateIsAnimationRunning(false))
                            if (currentChapter + 1 < bookInfo.totalChapter) {
                                onAutoScrollAction(AutoScrollAction.UpdateIsPaused(true))
                                onBookChapterContentAction(
                                    BookChapterContentAction.RequestScrollToChapter(currentChapter + 1)
                                )
                            } else {
                                onAutoScrollAction(AutoScrollAction.UpdateIsActivated(false))
                            }
                            break
                        }

                        onAutoScrollAction(AutoScrollAction.UpdateIsAnimationRunning(true))

                        listState.animateScrollBy(
                            value = bookChapterContentState.screenHeight.toFloat(),
                            animationSpec = tween(
                                durationMillis = currentAutoScrollState.autoScrollSpeed,
                                easing = LinearEasing
                            )
                        )

                        onAutoScrollAction(AutoScrollAction.UpdateIsAnimationRunning(false))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (!currentAutoScrollState.isActivated) {
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    if (!bookChapterContentState.enableUndoButton) {
                                        onBookChapterContentAction(BookChapterContentAction.UpdateSystemBar)
                                    }
                                },
                            )
                        } else {
                            Modifier.combinedClickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    if (!bookChapterContentState.enableUndoButton) {
                                        onBookChapterContentAction(BookChapterContentAction.UpdateSystemBar)
                                    }
                                },
                                onDoubleClick = {
                                    onAutoScrollAction(AutoScrollAction.UpdateIsPaused(false))
                                }
                            )
                        }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(color = stylingState.containerColor),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(
                                PaddingValues(
                                    start = WindowInsets.safeContent
                                        .only(WindowInsetsSides.Start)
                                        .asPaddingValues()
                                        .calculateStartPadding(LayoutDirection.Ltr),
                                )
                            )
                            .padding(start = 4.dp, end = 4.dp)
                            .weight(1f),
                        text = chapterData.chapterTitle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = TextStyle(
                            color = stylingState.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                        )
                    )
                    Text(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(
                                PaddingValues(
                                    end = WindowInsets.safeContent
                                        .only(WindowInsetsSides.End)
                                        .asPaddingValues()
                                        .calculateEndPadding(LayoutDirection.Ltr),
                                )
                            )
                            .padding(start = 4.dp, end = 4.dp)
                            .wrapContentWidth(),
                        text = "${currentChapter + 1} / ${bookInfo.totalChapter}",
                        style = TextStyle(
                            color = stylingState.textColor,
                            textAlign = TextAlign.Right,
                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                        ),
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .graphicsLayer {
                            val zoom = originalZoom
                            val offset = originalOffset
                            translationX = offset.x
                            translationY = offset.y
                            scaleX = zoom
                            scaleY = zoom
                        }
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown()
                                do {
                                    val event = awaitPointerEvent()
                                    var zoom = originalZoom
                                    zoom *= event.calculateZoom()
                                    zoom = zoom.coerceIn(1f, 10f)
                                    originalZoom = zoom
                                    val pan = event.calculatePan()
                                    val currentOffset = if (zoom == 1f) {
                                        Offset.Zero
                                    } else {
                                        val temp = originalOffset + pan.times(zoom)
                                        val maxX = (size.width * (zoom - 1) / 2f)
                                        val maxY = (size.height * (zoom - 1) / 2f)
                                        Offset(
                                            temp.x.coerceIn(-maxX, maxX),
                                            temp.y.coerceIn(-maxY, maxY)
                                        )
                                    }
                                    originalOffset = currentOffset
                                } while (event.changes.any { it.pressed })
                            }
                        }
                        .onSizeChanged {
                            size = it
                        }
                        .onGloballyPositioned { coordinates ->
                            onBookChapterContentAction(
                                BookChapterContentAction.UpdateScreenWidth(
                                    coordinates.size.width - (with(
                                        density
                                    ) { 32.dp.toPx() }.toInt())
                                )
                            )
                            onBookChapterContentAction(
                                BookChapterContentAction.UpdateScreenHeight(
                                    coordinates.size.height
                                )
                            )
                        },
                    contentPadding = PaddingValues(
                        start = WindowInsets.systemBars
                            .union(WindowInsets.displayCutout)
                            .only(WindowInsetsSides.Start)
                            .asPaddingValues()
                            .calculateStartPadding(LayoutDirection.Ltr),
                        end = WindowInsets.systemBars
                            .union(WindowInsets.displayCutout)
                            .only(WindowInsetsSides.End)
                            .asPaddingValues()
                            .calculateEndPadding(LayoutDirection.Ltr),
                    ),
                ) {
                    itemsIndexed(paragraphs) { index, paragraph ->
                        ChapterContent(
                            paragraph = paragraph,
                            stylingState = stylingState,
                            isHighlighted = ttsPlaybackState.isSpeaking && ttsPlaybackState.paragraphIndex == index
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(color = stylingState.containerColor),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    start = WindowInsets.safeContent
                                        .only(WindowInsetsSides.Start)
                                        .asPaddingValues()
                                        .calculateStartPadding(LayoutDirection.Ltr),
                                    bottom = WindowInsets.systemBars
                                        .only(WindowInsetsSides.Bottom)
                                        .asPaddingValues()
                                        .calculateBottomPadding()
                                )
                            ),
                        text = "${bookChapterContentState.lastVisibleItemIndex + 1} / ${paragraphs.size}",
                        style = TextStyle(
                            color = stylingState.textColor,
                            textAlign = TextAlign.Right,
                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                        ),
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        UiState.Empty -> {

        }
    }
}