package com.inkspire.ebookreader.ui.bookcontent.root

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.common.BookImporter
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.navigation.Navigator
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollAction
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarAction
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll.BottomBarAutoScrollAction
import com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll.BottomBarAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.bottombar.tts.BottomBarTTSAction
import com.inkspire.ebookreader.ui.bookcontent.bottombar.tts.BottomBarTTSViewModel
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentAction
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentRootScreen
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.composable.KeepScreenOn
import com.inkspire.ebookreader.ui.bookcontent.composable.PushDrawer
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerRoot
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark.BookmarkViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentAction
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
    val tableOfContentViewModel = koinViewModel<TableOfContentViewModel>()
    val noteViewModel = koinViewModel<NoteViewModel>(parameters = { parametersOf(bookId) })
    val bookmarkListViewModel = koinViewModel<BookmarkViewModel>()
    val topBarViewModel = koinViewModel<BookContentTopBarViewModel>()
    val bottomBarViewModel = koinViewModel<BookContentBottomBarViewModel>()
    val bottomBarTTSViewModel = koinViewModel<BottomBarTTSViewModel>(parameters = { parametersOf(bookId) })
    val bottomBarAutoScrollViewModel = koinViewModel<BottomBarAutoScrollViewModel>()
    val settingViewModel = koinViewModel<SettingViewModel>()
    val ttsViewModel = koinViewModel<TTSViewModel>()
    val autoScrollViewModel = koinViewModel<AutoScrollViewModel>()

    val bookContentDataState by dataViewModel.state.collectAsStateWithLifecycle()
    val drawerState by drawerViewModel.state.collectAsStateWithLifecycle()
    val stylingState by stylingViewModel.state.collectAsStateWithLifecycle()
    val bookChapterContentState by chapterContentViewModel.state.collectAsStateWithLifecycle()
    val tableOfContentState by tableOfContentViewModel.state.collectAsStateWithLifecycle()
    val noteState by noteViewModel.state.collectAsStateWithLifecycle()
    val bookmarkListState by bookmarkListViewModel.state.collectAsStateWithLifecycle()
    val bookContentTopBarState by topBarViewModel.state.collectAsStateWithLifecycle()
    val bookContentBottomBarState by bottomBarViewModel.state.collectAsStateWithLifecycle()
    val bottomBarTTSState by bottomBarTTSViewModel.state.collectAsStateWithLifecycle()
    val bottomBarAutoScrollState by bottomBarAutoScrollViewModel.state.collectAsStateWithLifecycle()
    val settingState by settingViewModel.state.collectAsStateWithLifecycle()
    val ttsPlaybackState by ttsViewModel.state.collectAsStateWithLifecycle()
    val highlightRange by ttsViewModel.currentHighlightRange.collectAsStateWithLifecycle()
    val readingOffset by ttsViewModel.currentReadingWordOffset.collectAsStateWithLifecycle()
    val autoScrollState by autoScrollViewModel.state.collectAsStateWithLifecycle()

    val bookChapterContentEvent = chapterContentViewModel.event
    val ttsUiEvent = ttsViewModel.event

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                    color = stylingState.textColor,
                    fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                    fontSize = stylingState.fontSize.sp
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
                    color = stylingState.textColor,
                    fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                    fontSize = stylingState.fontSize.sp
                )
            }
        }

        is UiState.Success -> {
            val bookInfo = state.data
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
            when (val state = bookContentDataState.tableOfContentState) {
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
                            color = stylingState.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                            fontSize = stylingState.fontSize.sp
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
                            color = stylingState.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                            fontSize = stylingState.fontSize.sp
                        )
                    }
                }

                is UiState.Success -> {
                    val tableOfContents = state.data

                    if (!settingState.keepScreenOn) {
                        if (autoScrollState.isActivated)
                            KeepScreenOn(true)
                        else
                            KeepScreenOn(false)
                    } else {
                        KeepScreenOn(true)
                    }

                    PushDrawer(
                        drawerState = drawerState,
                        stylingState = stylingState,
                        onDrawerAction = drawerViewModel::onAction,
                        drawerContent = {
                            DrawerRoot(
                                bookInfo = bookInfo,
                                tableOfContents = tableOfContents,
                                drawerState = drawerState,
                                stylingState = stylingState,
                                bookChapterContentState = bookChapterContentState,
                                tableOfContentState = tableOfContentState,
                                noteState = noteState,
                                bookmarkListState = bookmarkListState,
                                onDrawerAction = drawerViewModel::onAction,
                                onTableOfContentAction = {
                                    when (it) {
                                        is TableOfContentAction.NavigateToChapter -> {
                                            drawerViewModel.onAction(DrawerAction.CloseDrawer)
                                            chapterContentViewModel.onAction(BookChapterContentAction.RequestScrollToChapter(it.chapterIndex))
                                        }
                                        is TableOfContentAction.NavigateToParagraph -> {
                                            drawerViewModel.onAction(DrawerAction.CloseDrawer)
                                            chapterContentViewModel.onAction(BookChapterContentAction.RequestScrollToParagraph(it.chapterIndex, it.paragraphIndex))
                                        }
                                        else -> {
                                            tableOfContentViewModel.onAction(it)
                                        }
                                    }
                                },
                                onBookmarkListAction = bookmarkListViewModel::onAction,
                                onNoteAction = noteViewModel::onAction
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
                                    bookInfo = bookInfo,
                                    initialChapter = bookChapterContentState.currentChapterIndex,
                                    initialParagraph = bookChapterContentState.firstVisibleItemIndex,
                                    tableOfContents = tableOfContents,
                                    bookContentDataState = bookContentDataState,
                                    bookChapterContentState = bookChapterContentState,
                                    bookContentTopBarState = bookContentTopBarState,
                                    bookContentBottomBarState = bookContentBottomBarState,
                                    stylingState = stylingState,
                                    drawerState = drawerState,
                                    settingState = settingState,
                                    ttsPlaybackState = ttsPlaybackState,
                                    highlightRange = { highlightRange },
                                    readingOffset = { readingOffset },
                                    autoScrollState = autoScrollState,
                                    bottomBarTTSState = bottomBarTTSState,
                                    bottomBarAutoScrollState = bottomBarAutoScrollState,
                                    bookChapterContentEvent = bookChapterContentEvent,
                                    onAutoScrollAction = autoScrollViewModel::onAction,
                                    onTTSAction = ttsViewModel::onAction,
                                    onNoteAction = noteViewModel::onAction,
                                    onBookContentDataAction = {
                                        when (it) {
                                            is BookContentDataAction.CheckForNewChapter -> {
                                                BookImporter(
                                                    context = context,
                                                    scope = scope,
                                                    specialIntent = "null",
                                                ).fetchNewChapter(bookInfo)
                                            }
                                            else -> {
                                                dataViewModel.onAction(it)
                                            }
                                        }
                                    },
                                    onBookChapterContentAction = {
                                        when (it) {
                                            is BookChapterContentAction.UpdateSystemBar -> {
                                                topBarViewModel.onAction(BookContentTopBarAction.ChangeTopBarVisibility)
                                                bottomBarViewModel.onAction(BookContentBottomBarAction.ChangeBottomBarVisibility)
                                            }
                                            else -> {
                                                chapterContentViewModel.onAction(it)
                                            }
                                        }
                                    },
                                    onBookContentTopBarAction = {
                                        when (it) {
                                            BookContentTopBarAction.BackIconClicked -> {
                                                if (ttsPlaybackState.isActivated) {
                                                    ttsViewModel.onAction(TTSAction.OnStopClick)
                                                }
                                                parentNavigator.handleBack()
                                            }
                                            BookContentTopBarAction.BookmarkIconClicked -> {
                                                tableOfContentViewModel.onAction(
                                                    TableOfContentAction.UpdateCurrentChapterFavoriteState(
                                                        bookId = bookId,
                                                        chapterIndex = bookChapterContentState.currentChapterIndex,
                                                        isFavorite = !tableOfContents[bookChapterContentState.currentChapterIndex].isFavorite
                                                    )
                                                )
                                            }
                                            BookContentTopBarAction.DrawerIconClicked -> {
                                                drawerViewModel.onAction(DrawerAction.OpenDrawer)
                                            }
                                            else -> {
                                                topBarViewModel.onAction(it)
                                            }
                                        }
                                    },
                                    onBookContentBottomBarAction = {
                                        when (it) {
                                            is BookContentBottomBarAction.TtsIconClicked -> {
                                                ttsViewModel.onAction(TTSAction.StartTTS(bookChapterContentState.firstVisibleItemIndex))
                                                bottomBarViewModel.onAction(it)
                                            }
                                            is BookContentBottomBarAction.AutoScrollIconClicked -> {
                                                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsActivated(true))
                                                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsPaused(false))
                                                bottomBarViewModel.onAction(BookContentBottomBarAction.ChangeBottomBarVisibility)
                                                topBarViewModel.onAction(BookContentTopBarAction.ChangeTopBarVisibility)
                                                bottomBarViewModel.onAction(it)
                                            }
                                            else -> {
                                                bottomBarViewModel.onAction(it)
                                            }
                                        }
                                    },
                                    onBookContentBottomBarTTSAction = {
                                        when (it) {
                                            is BottomBarTTSAction.OnNextChapterClicked -> {
                                                ttsViewModel.onAction(TTSAction.OnPlayNextChapterClick)
                                            }
                                            is BottomBarTTSAction.OnNextParagraphClicked -> {
                                                ttsViewModel.onAction(TTSAction.OnPlayNextParagraphClick)
                                            }
                                            is BottomBarTTSAction.OnPlayPauseClicked -> {
                                                ttsViewModel.onAction(TTSAction.OnPlayPauseClick)
                                            }
                                            is BottomBarTTSAction.OnPreviousChapterClicked -> {
                                                ttsViewModel.onAction(TTSAction.OnPlayPreviousChapterClick)
                                            }
                                            is BottomBarTTSAction.OnPreviousParagraphClicked -> {
                                                ttsViewModel.onAction(TTSAction.OnPlayPreviousParagraphClick)
                                            }
                                            is BottomBarTTSAction.OnStopClicked -> {
                                                ttsViewModel.onAction(TTSAction.OnStopClick)
                                                bottomBarViewModel.onAction(BookContentBottomBarAction.ResetBottomBarMode)
                                            }
                                            else -> {
                                                bottomBarTTSViewModel.onAction(it)
                                            }
                                        }
                                    },
                                    onBookContentBottomBarAutoScrollAction = {
                                        when (it) {
                                            is BottomBarAutoScrollAction.StopIconClicked -> {
                                                bottomBarViewModel.onAction(BookContentBottomBarAction.ResetBottomBarMode)
                                                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsActivated(false))
                                                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsPaused(false))
                                            }
                                            is BottomBarAutoScrollAction.PlayPauseIconClicked -> {
                                                autoScrollViewModel.onAction(AutoScrollAction.UpdateIsPaused(it.isPaused))
                                                bottomBarViewModel.onAction(BookContentBottomBarAction.ChangeBottomBarVisibility)
                                                topBarViewModel.onAction(BookContentTopBarAction.ChangeTopBarVisibility)
                                            }
                                            else -> {
                                                bottomBarAutoScrollViewModel.onAction(it)
                                            }
                                        }
                                    },
                                    onBookContentBottomBarSettingAction = settingViewModel::onAction,
                                    onBookContentBottomBarStyleAction = stylingViewModel::onAction
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