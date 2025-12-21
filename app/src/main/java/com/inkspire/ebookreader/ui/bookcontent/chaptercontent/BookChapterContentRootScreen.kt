package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.TableOfContent
import com.inkspire.ebookreader.ui.bookcontent.composable.CustomFab
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataAction
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataState
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBar
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarAction
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookChapterContentRootScreen(
    bookInfo: Book,
    tableOfContents: List<TableOfContent>,
    bookContentDataState: BookContentDataState,
    bookChapterContentState: BookChapterContentState,
    bookContentTopBarState: BookContentTopBarState,
    stylingState: StylingState,
    drawerState: DrawerState,
    onBookContentDataAction: (BookContentDataAction) -> Unit,
    onBookChapterContentAction: (BookChapterContentAction) -> Unit,
    onBookContentTopBarAction: (BookContentTopBarAction) -> Unit,
) {
    val initialPage = bookInfo.currentChapter
    val initialChapterIndex = maxOf(0, initialPage)
    val totalChapters = bookInfo.totalChapter
    val pagerState = rememberPagerState(
        initialPage = initialChapterIndex,
        pageCount = { totalChapters }
    )
    val pageStates = remember { mutableStateMapOf<Int, LazyListState>() }
    var isAutoScrolling by rememberSaveable { mutableStateOf(false) }
    val hazeState = rememberHazeState()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                if (page != bookInfo.currentChapter) {
                    onBookContentDataAction(BookContentDataAction.UpdateRecentChapterToDB(page))
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
        onBookChapterContentAction(BookChapterContentAction.UpdateCurrentChapter(pagerState.targetPage))
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BookContentTopBar(
                tableOfContents = tableOfContents,
                hazeState = hazeState,
                stylingState = stylingState,
                drawerState = drawerState,
                bookChapterContentState = bookChapterContentState,
                bookContentTopBarState = bookContentTopBarState,
                onAction = onBookContentTopBarAction
            )
        },
        bottomBar = {

        },
        floatingActionButton = {
            if (bookChapterContentState.enableUndoButton) {
                CustomFab(
                    stylingState = stylingState,
                    onFabClick = {
                        onBookChapterContentAction(BookChapterContentAction.UpdateEnableUndoButton(false))
                    }
                )
            }
        },
        containerColor = Color.Transparent,
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState),
                color = stylingState.backgroundColor
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = bookChapterContentState.enablePagerScroll
                ) { pageIndex ->
                    BookChapterContent(
                        bookInfo = bookInfo,
                        currentChapter = pageIndex,
                        stylingState = stylingState,
                        bookChapterContentState = bookChapterContentState,
                        chapterUiState = bookContentDataState.chapterStates[pageIndex] ?: UiState.None,
                        isCurrentChapter = pagerState.currentPage == pageIndex,
                        onBookContentDataAction = onBookContentDataAction,
                        onBookChapterContentAction = onBookChapterContentAction,
                        onListStateLoaded = { loadedState ->
                            pageStates[pageIndex] = loadedState
                        },
                        onDispose = {
                            pageStates.remove(pageIndex)
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    )
}