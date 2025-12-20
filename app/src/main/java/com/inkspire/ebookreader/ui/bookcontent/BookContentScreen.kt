package com.inkspire.ebookreader.ui.bookcontent

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.ui.composable.MyLoadingAnimation
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookContentScreen(
    bookContentState: BookContentState,
    contentState: ContentState,
    onAction: (BookContentAction) -> Unit,
) {
    when (val state = bookContentState.bookState) {
        is UiState.None -> {

        }
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
                            onAction(BookContentAction.UpdateRecentChapterToDB(page))
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
            LaunchedEffect(pagerState.targetPage) {
                onAction(BookContentAction.UpdateCurrentChapter(pagerState.targetPage))
            }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {

                },
                bottomBar = {

                },
                content = {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { pageIndex ->
                        BookChapterContent(
                            contentState = contentState,
                            chapterUiState = bookContentState.chapterStates[pageIndex] ?: UiState.None,
                            bookInfo = book,
                            currentChapter = pageIndex,
                            isCurrentChapter = pagerState.currentPage == pageIndex,
                            onAction = onAction,
                            onListStateLoaded = { loadedState ->
                                pageStates[pageIndex] = loadedState
                            },
                            onDispose = {
                                pageStates.remove(pageIndex)
                            }
                        )
                    }
                }
            )
        }
    }
}