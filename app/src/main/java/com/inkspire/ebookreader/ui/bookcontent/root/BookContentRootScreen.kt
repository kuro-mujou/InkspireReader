package com.inkspire.ebookreader.ui.bookcontent.root

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.navigation.Navigator
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollAction
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarAction
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll.BottomBarAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.tts.BottomBarTTSViewModel
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentAction
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentRootScreen
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.CombineActions
import com.inkspire.ebookreader.ui.bookcontent.common.LocalAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalBookmarkViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalBottomBarAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalBottomBarTTSViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalBottomBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalChapterContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalCombineActions
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDataViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalNoteViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalSettingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTTSViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTableOfContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTopBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.composable.PushDrawer
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerRoot
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark.BookmarkViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.styling.BookContentStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarAction
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSAction
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSUiEvent
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSViewModel
import com.inkspire.ebookreader.ui.composable.MyLoadingAnimation
import com.inkspire.ebookreader.ui.setting.SettingViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BookContentRootScreen(
    bookId: String,
    parentNavigator: Navigator
) {
    val dataViewModel = koinViewModel<BookContentDataViewModel>(parameters = { parametersOf(bookId) })
    val drawerViewModel = koinViewModel<DrawerViewModel>()
    val stylingViewModel = koinViewModel<BookContentStylingViewModel>()
    val chapterContentViewModel = koinViewModel<BookChapterContentViewModel>()
    val tableOfContentViewModel = koinViewModel<TableOfContentViewModel>(parameters = { parametersOf(bookId) })
    val noteViewModel = koinViewModel<NoteViewModel>(parameters = { parametersOf(bookId) })
    val bookmarkListViewModel = koinViewModel<BookmarkViewModel>()
    val topBarViewModel = koinViewModel<BookContentTopBarViewModel>()
    val bottomBarViewModel = koinViewModel<BookContentBottomBarViewModel>()
    val bottomBarTTSViewModel = koinViewModel<BottomBarTTSViewModel>(parameters = { parametersOf(bookId) })
    val bottomBarAutoScrollViewModel = koinViewModel<BottomBarAutoScrollViewModel>()
    val settingViewModel = koinViewModel<SettingViewModel>()
    val ttsViewModel = koinViewModel<TTSViewModel>(parameters = { parametersOf(true) })
    val autoScrollViewModel = koinViewModel<AutoScrollViewModel>()

    val combineActions = remember (
        drawerViewModel,
        chapterContentViewModel,
        topBarViewModel,
        bottomBarViewModel,
        ttsViewModel,
        autoScrollViewModel,
        parentNavigator
    ) {
        object : CombineActions {
            override fun navigateToChapter(chapterIndex: Int) {
                drawerViewModel.onAction(DrawerAction.CloseDrawer)
                chapterContentViewModel.onAction(BookChapterContentAction.RequestScrollToChapter(chapterIndex))
                ttsViewModel.onAction(TTSAction.OnNavigateToRandomChapter(chapterIndex))
            }

            override fun navigateToParagraph(chapterIndex: Int, paragraphIndex: Int) {
                drawerViewModel.onAction(DrawerAction.CloseDrawer)
                chapterContentViewModel.onAction(BookChapterContentAction.RequestScrollToParagraph(chapterIndex, paragraphIndex))
            }

            override fun updateSystemBarVisibility() {
                topBarViewModel.onAction(BookContentTopBarAction.ChangeTopBarVisibility)
                bottomBarViewModel.onAction(BookContentBottomBarAction.ChangeBottomBarVisibility)
            }

            override fun onBackClicked() {
                ttsViewModel.onAction(TTSAction.OnStopClick)
                parentNavigator.handleBack()
            }

            override fun onTTSActivated(firstVisibleItemIndex: Int) {
                ttsViewModel.onAction(TTSAction.StartTTS(firstVisibleItemIndex))
                bottomBarViewModel.onAction(BookContentBottomBarAction.TtsIconClicked)
            }

            override fun onPreviousChapterClicked() {
                ttsViewModel.onAction(TTSAction.OnPlayPreviousChapterClick)
            }

            override fun onNextChapterClicked() {
                ttsViewModel.onAction(TTSAction.OnPlayNextChapterClick)
            }

            override fun onPreviousParagraphClicked() {
                ttsViewModel.onAction(TTSAction.OnPlayPreviousParagraphClick)
            }

            override fun onNextParagraphClicked() {
                ttsViewModel.onAction(TTSAction.OnPlayNextParagraphClick)
            }

            override fun onToggleTTS() {
                ttsViewModel.onAction(TTSAction.OnPlayPauseClick)
            }

            override fun onStopTTS() {
                ttsViewModel.onAction(TTSAction.OnStopClick)
                bottomBarViewModel.onAction(BookContentBottomBarAction.ResetBottomBarMode)
            }

            override fun onAutoScrollActivated() {
                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsActivated(true))
                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsPaused(false))
                topBarViewModel.onAction(BookContentTopBarAction.ChangeTopBarVisibility)
                bottomBarViewModel.onAction(BookContentBottomBarAction.ChangeBottomBarVisibility)
                bottomBarViewModel.onAction(BookContentBottomBarAction.AutoScrollIconClicked)
            }

            override fun onStopAutoScroll() {
                bottomBarViewModel.onAction(BookContentBottomBarAction.ResetBottomBarMode)
                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsActivated(false))
                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsPaused(false))
            }

            override fun onToggleAutoScroll(isPaused: Boolean) {
                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsPaused(isPaused))
                bottomBarViewModel.onAction(BookContentBottomBarAction.ChangeBottomBarVisibility)
                topBarViewModel.onAction(BookContentTopBarAction.ChangeTopBarVisibility)
            }
        }
    }

    CompositionLocalProvider(
        LocalCombineActions provides combineActions,
        LocalDataViewModel provides dataViewModel,
        LocalDrawerViewModel provides drawerViewModel,
        LocalStylingViewModel provides stylingViewModel,
        LocalTableOfContentViewModel provides tableOfContentViewModel,
        LocalTopBarViewModel provides topBarViewModel,
        LocalBottomBarViewModel provides bottomBarViewModel,
        LocalBottomBarTTSViewModel provides bottomBarTTSViewModel,
        LocalBottomBarAutoScrollViewModel provides bottomBarAutoScrollViewModel,
        LocalNoteViewModel provides noteViewModel,
        LocalBookmarkViewModel provides bookmarkListViewModel,
        LocalAutoScrollViewModel provides autoScrollViewModel,
        LocalTTSViewModel provides ttsViewModel,
        LocalChapterContentViewModel provides chapterContentViewModel,
        LocalSettingViewModel provides settingViewModel
    ) {
        val bookContentDataState by dataViewModel.state.collectAsStateWithLifecycle()
        val stylingState by stylingViewModel.state.collectAsStateWithLifecycle()
        val bookChapterContentState by chapterContentViewModel.state.collectAsStateWithLifecycle()
        val ttsUiEvent = ttsViewModel.event

        when (val state = bookContentDataState.bookState) {
            is UiState.None -> {

            }

            is UiState.Loading -> {
                MyLoadingAnimation(
                    stylingState = stylingState
                )
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error while loading book",
                        color = stylingState.stylePreferences.textColor,
                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                        fontSize = stylingState.stylePreferences.fontSize.sp
                    )
                }
            }

            is UiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Data might corrupted, please re-import this book",
                        color = stylingState.stylePreferences.textColor,
                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                        fontSize = stylingState.stylePreferences.fontSize.sp
                    )
                }
            }

            is UiState.Success -> {
                val bookInfo = state.data()
                BackHandler(true) {}
                LaunchedEffect(bookInfo) {
                    chapterContentViewModel.onAction(
                        BookChapterContentAction.InitFromDatabase(
                            chapter = bookInfo.currentChapter,
                            paragraph = bookInfo.currentParagraph
                        )
                    )
                    ttsViewModel.onAction(TTSAction.SetBookInfo(bookInfo))
                }
                when (bookContentDataState.tableOfContentState) {
                    is UiState.None -> {

                    }

                    is UiState.Loading -> {
                        MyLoadingAnimation(
                            stylingState = stylingState
                        )
                    }

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error while loading book",
                                color = stylingState.stylePreferences.textColor,
                                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                                fontSize = stylingState.stylePreferences.fontSize.sp
                            )
                        }
                    }

                    is UiState.Empty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Data might corrupted, please re-import this book",
                                color = stylingState.stylePreferences.textColor,
                                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                                fontSize = stylingState.stylePreferences.fontSize.sp
                            )
                        }
                    }

                    is UiState.Success -> {
                        PushDrawer(
                            drawerContent = {
                                DrawerRoot(
                                    bookInfoProvider = { bookInfo },
                                )
                            },
                            mainContent = {
                                if (bookChapterContentState.currentChapterIndex != -1) {
                                    LaunchedEffect(ttsUiEvent) {
                                        ttsUiEvent.collect { event ->
                                            when (event) {
                                                is TTSUiEvent.StopReading -> {
                                                    bottomBarViewModel.onAction(BookContentBottomBarAction.ResetBottomBarMode)
                                                }
                                            }
                                        }
                                    }
                                    BookChapterContentRootScreen(
                                        bookInfoProvider = { bookInfo },
                                    )
                                } else {
                                    MyLoadingAnimation(
                                        stylingState = stylingState
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}