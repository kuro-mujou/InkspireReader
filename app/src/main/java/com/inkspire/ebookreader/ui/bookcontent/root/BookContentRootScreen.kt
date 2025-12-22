package com.inkspire.ebookreader.ui.bookcontent.root

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.navigation.Navigator
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarAction
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentAction
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentRootScreen
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.composable.PushDrawer
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerRoot
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.styling.BookContentStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarAction
import com.inkspire.ebookreader.ui.bookcontent.topbar.BookContentTopBarViewModel
import com.inkspire.ebookreader.ui.composable.MyLoadingAnimation
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
    val topBarViewModel = koinViewModel<BookContentTopBarViewModel>()
    val bottomBarViewModel = koinViewModel<BookContentBottomBarViewModel>()

    val bookContentDataState by dataViewModel.state.collectAsStateWithLifecycle()
    val drawerState by drawerViewModel.state.collectAsStateWithLifecycle()
    val stylingState by stylingViewModel.state.collectAsStateWithLifecycle()
    val bookChapterContentState by chapterContentViewModel.state.collectAsStateWithLifecycle()
    val tableOfContentState by tableOfContentViewModel.state.collectAsStateWithLifecycle()
    val bookContentTopBarState by topBarViewModel.state.collectAsStateWithLifecycle()
    val bookContentBottomBarState by bottomBarViewModel.state.collectAsStateWithLifecycle()

    val bookChapterContentEvent = chapterContentViewModel.event

    when (val state = bookContentDataState.bookState) {
        is UiState.None -> {

        }

        is UiState.Loading -> {
            MyLoadingAnimation(
                stylingState = stylingState
            )
        }

        is UiState.Error -> {
            Text(text = "Error loading book")
        }

        is UiState.Empty -> {
            Text(text = "No book found")
        }

        is UiState.Success -> {
            val bookInfo = state.data
            LaunchedEffect(bookInfo) {
                chapterContentViewModel.onAction(
                    BookChapterContentAction.InitFromDatabase(
                        chapter = bookInfo.currentChapter,
                        paragraph = bookInfo.currentParagraph
                    )
                )
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
                    Text(text = "Error loading book")
                }

                is UiState.Empty -> {
                    Text(text = "No book found")
                }

                is UiState.Success -> {
                    val tableOfContents = state.data
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
                                onDrawerAction = drawerViewModel::onAction,
                                onTableOfContentAction = {
                                    when (it) {
                                        is TableOfContentAction.NavigateToChapter -> {
                                            drawerViewModel.onAction(DrawerAction.CloseDrawer)
                                            chapterContentViewModel.onAction(BookChapterContentAction.RequestScrollToChapter(it.chapterIndex))
                                        }
                                        else -> {}
                                    }
                                }
                            )
                        },
                        mainContent = {
                            if (bookChapterContentState.currentChapterIndex != -1) {
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
                                    bookChapterContentEvent = bookChapterContentEvent,
                                    onBookContentDataAction = dataViewModel::onAction,
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
                                    onBookContentBottomBarAction = bottomBarViewModel::onAction,
                                    onStyleAction = stylingViewModel::onAction
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