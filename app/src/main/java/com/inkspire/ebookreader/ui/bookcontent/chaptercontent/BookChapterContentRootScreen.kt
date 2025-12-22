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
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBar
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarAction
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarState
import com.inkspire.ebookreader.ui.bookcontent.composable.CustomFab
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataAction
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataState
import com.inkspire.ebookreader.ui.bookcontent.styling.BookContentStylingAction
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBar
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarAction
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookChapterContentRootScreen(
    bookInfo: Book,
    initialChapter: Int,
    initialParagraph: Int,
    tableOfContents: List<TableOfContent>,
    bookContentDataState: BookContentDataState,
    bookChapterContentState: BookChapterContentState,
    bookContentTopBarState: BookContentTopBarState,
    bookContentBottomBarState: BookContentBottomBarState,
    stylingState: StylingState,
    drawerState: DrawerState,
    bookChapterContentEvent: Flow<BookChapterContentEvent>,
    onBookContentDataAction: (BookContentDataAction) -> Unit,
    onBookChapterContentAction: (BookChapterContentAction) -> Unit,
    onBookContentTopBarAction: (BookContentTopBarAction) -> Unit,
    onBookContentBottomBarAction: (BookContentBottomBarAction) -> Unit,
    onStyleAction: (BookContentStylingAction) -> Unit
) {
    val totalChapters = bookInfo.totalChapter
    val pagerState = rememberPagerState(
        initialPage = initialChapter,
        pageCount = { totalChapters }
    )
    val lazyListStates = remember { mutableStateMapOf<Int, LazyListState>() }
    var isAutoScrolling by rememberSaveable { mutableStateOf(false) }
    val hazeState = rememberHazeState()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .drop(1)
            .collect { page ->
                onBookContentDataAction(BookContentDataAction.UpdateRecentChapterToDB(page))
            }
    }
    LaunchedEffect(isAutoScrolling, pagerState.currentPage) {
        if (isAutoScrolling) {
            while (true) {
                val currentListState = lazyListStates[pagerState.currentPage]
                currentListState?.scrollBy(2f)
                delay(16)
            }
        }
    }
    LaunchedEffect(pagerState.targetPage) {
        onBookChapterContentAction(BookChapterContentAction.UpdateCurrentChapter(pagerState.targetPage))
    }
    LaunchedEffect(bookChapterContentEvent) {
        bookChapterContentEvent.collect { event ->
            when (event) {
                is BookChapterContentEvent.ScrollToChapter -> {
                    pagerState.scrollToPage(event.page)
                }
                is BookChapterContentEvent.ScrollToParagraph -> {
                    pagerState.scrollToPage(event.page)
                    val currentListState = lazyListStates[event.page]
                    currentListState?.scrollToItem(event.paragraphIndex)
                }
            }
        }
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
            BookContentBottomBar(
                bookInfo = bookInfo,
                tableOfContents = tableOfContents,
                hazeState = hazeState,
                stylingState = stylingState,
                drawerState = drawerState,
                bookChapterContentState = bookChapterContentState,
                bookContentBottomBarState = bookContentBottomBarState,
                onBookContentBottomBarAction = onBookContentBottomBarAction,
                onStyleAction = onStyleAction
            )
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
                    userScrollEnabled = bookChapterContentState.enablePagerScroll,
                    beyondViewportPageCount = 1
                ) { pageIndex ->
                    val paragraphToLoad = if (pageIndex == initialChapter) initialParagraph else 0
                    BookChapterContent(
                        bookInfo = bookInfo,
                        initialParagraphIndex = paragraphToLoad,
                        currentChapter = pageIndex,
                        stylingState = stylingState,
                        bookChapterContentState = bookChapterContentState,
                        chapterUiState = bookContentDataState.chapterStates[pageIndex] ?: UiState.None,
                        isCurrentChapter = pagerState.currentPage == pageIndex,
                        onBookContentDataAction = onBookContentDataAction,
                        onBookChapterContentAction = onBookChapterContentAction,
                        onListStateLoaded = { loadedState ->
                            lazyListStates[pageIndex] = loadedState
                        },
                        onDispose = {
                            lazyListStates.remove(pageIndex)
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    )
}