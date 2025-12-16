package com.inkspire.ebookreader.ui.bookcontent

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.ui.composable.MyLoadingAnimation
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookContentScreen(
    paddingValues: PaddingValues
) {
    val viewModel = koinViewModel<BookContentViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState.bookState) {
        is UiState.Loading -> {
            MyLoadingAnimation()
        }
        is UiState.Error -> {
            Text(text = "Error loading book content")
        }
        is UiState.Empty -> {
            Text(text = "No book content available")
        }
        is UiState.Success -> {
            val book = state.data
            val initialPage = book.currentChapter
            val initialChapterIndex = maxOf(0, initialPage)
            val totalChapters = book.totalChapter
            val pagerState = rememberPagerState(
                initialPage = initialChapterIndex,
                pageCount = { totalChapters }
            )
            val pageStates = remember { mutableStateMapOf<Int, LazyListState>() }
            var isAutoScrolling by rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .collect { page ->
                        if (page != book.currentChapter) {
                            viewModel.updateRecentChapter(page)
                        }
                    }
            }
            LaunchedEffect(isAutoScrolling, pagerState.currentPage) {
                if (isAutoScrolling) {
                    while (true) {
                        val currentListState = pageStates[pagerState.currentPage]
                        currentListState?.scrollBy(2f)
                        delay(16)
                    }
                }
            }
            Column {
                 Button(onClick = { isAutoScrolling = !isAutoScrolling }) {
                     Text(if(isAutoScrolling) "Stop Scroll" else "Start Scroll")
                 }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->
                    val initialParagraph = if (pageIndex == book.currentChapter) {
                        book.currentParagraph
                    } else {
                        0
                    }

                    val isCurrentPage = (pagerState.currentPage == pageIndex)

                    SingleChapterPage(
                        chapterIndex = pageIndex,
                        initialParagraphIndex = initialParagraph,
                        viewModel = viewModel,
                        isCurrentPage = isCurrentPage,
                        onScrollPositionChanged = { newParagraphIndex ->
                            viewModel.updateRecentParagraph(newParagraphIndex)
                        },
                        onListStateLoaded = { loadedState ->
                            pageStates[pageIndex] = loadedState
                        },
                        onDispose = {
                            pageStates.remove(pageIndex)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun SingleChapterPage(
    chapterIndex: Int,
    initialParagraphIndex: Int,
    viewModel: BookContentViewModel,
    isCurrentPage: Boolean,
    onScrollPositionChanged: (Int) -> Unit,
    onListStateLoaded: (LazyListState) -> Unit,
    onDispose: () -> Unit
) {
    val chapterUiState by produceState<UiState<Chapter>>(initialValue = UiState.Loading, key1 = chapterIndex) {
        viewModel.getChapterContent(chapterIndex).collect { value = it }
    }

    when (val state = chapterUiState) {
        is UiState.Loading -> {
            MyLoadingAnimation()
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.throwable.message}")
            }
        }
        is UiState.Success -> {
            val chapterData = state.data
            val paragraphs = chapterData.content

            val listState = rememberLazyListState(
                initialFirstVisibleItemIndex = initialParagraphIndex
            )

            DisposableEffect(listState) {
                onListStateLoaded(listState)
                onDispose {
                    onDispose()
                }
            }

            LaunchedEffect(listState, isCurrentPage) {
                snapshotFlow { listState.firstVisibleItemIndex }
                    .collect { firstVisibleItem ->
                        if (isCurrentPage) {
                            onScrollPositionChanged(firstVisibleItem)
                        }
                    }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(paragraphs) { paragraph ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = paragraph)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        UiState.Empty -> {

        }
    }
}