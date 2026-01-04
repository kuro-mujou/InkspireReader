package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.common.isSuccess
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBar
import com.inkspire.ebookreader.ui.bookcontent.common.LocalAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalChapterContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDataViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalSettingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTTSViewModel
import com.inkspire.ebookreader.ui.bookcontent.composable.CustomFab
import com.inkspire.ebookreader.ui.bookcontent.composable.KeepScreenOn
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataAction
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBar
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSAction
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.drop

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookChapterContentRootScreen(
    bookInfoProvider: () -> Book,
) {
    val drawerVM = LocalDrawerViewModel.current
    val dataVM = LocalDataViewModel.current
    val stylingVM = LocalStylingViewModel.current
    val settingVM = LocalSettingViewModel.current
    val autoScrollVM = LocalAutoScrollViewModel.current
    val chapterContentVM = LocalChapterContentViewModel.current
    val ttsVM = LocalTTSViewModel.current

    val drawerState by drawerVM.state.collectAsStateWithLifecycle()
    val bookContentDataState by dataVM.state.collectAsStateWithLifecycle()
    val stylingState by stylingVM.state.collectAsStateWithLifecycle()
    val settingState by settingVM.state.collectAsStateWithLifecycle()
    val autoScrollState by autoScrollVM.state.collectAsStateWithLifecycle()
    val bookChapterContentState by chapterContentVM.state.collectAsStateWithLifecycle()
    val ttsPlaybackState by ttsVM.state.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = bookChapterContentState.currentChapterIndex,
        pageCount = { bookInfoProvider().totalChapter }
    )
    val lazyListStates = remember { mutableStateMapOf<Int, LazyListState>() }
    val hazeState = rememberHazeState()
    val hazeEnableProvider = { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !drawerState.visibility && !drawerState.isAnimating }
    val tableOfContents = remember(bookContentDataState.tableOfContentState) {
        if (bookContentDataState.tableOfContentState.isSuccess) {
            (bookContentDataState.tableOfContentState as UiState.Success).data()
        } else {
            emptyList()
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .drop(1)
            .collect { page ->
                dataVM.onAction(BookContentDataAction.UpdateRecentChapterToDB(page))
            }
    }

    LaunchedEffect(pagerState.targetPage) {
        if (ttsPlaybackState.chapterIndex != pagerState.targetPage) {
            ttsVM.onAction(TTSAction.UpdateCurrentChapterData(
                pagerState.targetPage,
                bookChapterContentState.currentChapterIndex
            ))
        }
        chapterContentVM.onAction(BookChapterContentAction.UpdateCurrentChapterIndex(pagerState.targetPage))
    }

    LaunchedEffect(chapterContentVM.event) {
        chapterContentVM.event.collect { event ->
            when (event) {
                is BookChapterContentEvent.ScrollToChapter -> {
                    pagerState.scrollToPage(event.page)
                }
                is BookChapterContentEvent.ScrollToParagraph -> {
                    pagerState.scrollToPage(event.page)
                    val currentListState = lazyListStates[event.page]
                    currentListState?.scrollToItem(event.paragraphIndex)
                }
                is BookChapterContentEvent.AnimatedScrollToChapter -> {
                    pagerState.animateScrollToPage(event.page)
                }
            }
        }
    }

    LaunchedEffect(ttsPlaybackState.chapterIndex) {
        if (ttsPlaybackState.chapterIndex != -1 && ttsPlaybackState.chapterIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(ttsPlaybackState.chapterIndex)
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BookContentTopBar(
                hazeState = hazeState,
                hazeEnableProvider = hazeEnableProvider,
                currentChapterIndexProvider = { bookChapterContentState.currentChapterIndex }
            )
        },
        bottomBar = {
            BookContentBottomBar(
                hazeState = hazeState,
                hazeEnableProvider = hazeEnableProvider,
                bookInfoProvider = bookInfoProvider,
                chapterTitleProvider = { tableOfContents.getOrNull(bookChapterContentState.currentChapterIndex)?.title ?: "" },
                firstVisibleIndexProvider = { bookChapterContentState.firstVisibleItemIndex }
            )
        },
        floatingActionButton = {
            if (bookChapterContentState.enableUndoButton) {
                CustomFab(
                    stylingState = stylingState,
                    onFabClick = {
                        chapterContentVM.onAction(BookChapterContentAction.UpdateEnableUndoButton(false))
                    }
                )
            }
        },
        containerColor = Color.Transparent,
        content = {
            if (!settingState.readerSettings.keepScreenOn) {
                if (autoScrollState.isActivated)
                    KeepScreenOn(true)
                else
                    KeepScreenOn(false)
            } else {
                KeepScreenOn(true)
            }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState),
                color = stylingState.stylePreferences.backgroundColor
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = bookChapterContentState.enablePagerScroll && !ttsPlaybackState.isActivated,
                    beyondViewportPageCount = 1
                ) { pageIndex ->
                    val currentChapterUIState by remember{ derivedStateOf { bookContentDataState.chapterStates[pageIndex] ?: UiState.Empty } }
                    BookChapterContent(
                        bookInfoProvider = bookInfoProvider,
                        initialParagraphIndex = { if (pageIndex == bookChapterContentState.currentChapterIndex) bookChapterContentState.firstVisibleItemIndex else 0 },
                        isCurrentChapter = { pageIndex == pagerState.currentPage },
                        currentChapter = { pageIndex },
                        chapterUiState = { currentChapterUIState },
                        onListStateLoaded = { loadedState ->
                            lazyListStates[pageIndex] = loadedState
                        },
                        onDispose = {
                            lazyListStates.remove(pageIndex)
                        },
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    )
}