package com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.common.isSuccess
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.ui.bookcontent.common.LocalBookmarkViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalCombineActions
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDataViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.setting.bookmark.BookmarkSetting
import com.inkspire.ebookreader.ui.setting.bookmark.composable.MyBookmarkCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkList(
    bookInfoProvider: () -> Book,
) {
    val combineActions = LocalCombineActions.current
    val drawerVM = LocalDrawerViewModel.current
    val stylingVM = LocalStylingViewModel.current
    val bookmarkVM = LocalBookmarkViewModel.current
    val dataVM = LocalDataViewModel.current

    val dataState by dataVM.state.collectAsStateWithLifecycle()
    val drawerState by drawerVM.state.collectAsStateWithLifecycle()
    val stylingState by stylingVM.state.collectAsStateWithLifecycle()
    val bookmarkState by bookmarkVM.state.collectAsStateWithLifecycle()

    val bookmarkMenuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()
    val bookId by remember { derivedStateOf { bookInfoProvider().id } }
    val tableOfContents = remember(dataState.tableOfContentState) {
        if (dataState.tableOfContentState.isSuccess) {
            (dataState.tableOfContentState as UiState.Success).data().filter { it.isFavorite }
        } else {
            emptyList()
        }
    }

    LaunchedEffect(drawerState.visibility) {
        if (!drawerState.visibility) {
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    bookmarkVM.onAction(BookmarkListAction.UpdateBookmarkThemeSettingVisibility)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_setting),
                    contentDescription = null,
                    tint = stylingState.stylePreferences.textColor
                )
            }
            AnimatedVisibility(
                visible = bookmarkState.enableUndoDeleteBookmark
            ) {
                IconButton(
                    onClick = {
                        bookmarkVM.onAction(BookmarkListAction.UndoDeleteBookmark(bookId))
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
                        contentDescription = null,
                        tint = stylingState.stylePreferences.textColor
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
        ) {
            items(
                items = tableOfContents,
                key = { it.index }
            ) { bookMark ->
                MyBookmarkCard(
                    bookmarkContent = bookMark.title,
                    bookmarkIndex = bookMark.index,
                    bookmarkStyle = bookmarkState.readerSettings.bookmarkStyle,
                    deletable = true,
                    stylingState = stylingState,
                    onCardClicked = {
                        combineActions.navigateToChapter(bookMark.index)
                    },
                    onDeleted = {
                        bookmarkVM.onAction(BookmarkListAction.DeleteBookmark(bookId, bookMark.index))
                    }
                )
            }
        }
    }
    if (bookmarkState.bookmarkThemeSettingVisibility) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            dragHandle = {
                Surface(
                    modifier = Modifier
                        .padding(vertical = 22.dp),
                    color = stylingState.stylePreferences.textColor,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }
            },
            sheetState = bookmarkMenuSheetState,
            onDismissRequest = {
                bookmarkVM.onAction(BookmarkListAction.UpdateBookmarkThemeSettingVisibility)
            },
            containerColor = stylingState.stylePreferences.backgroundColor
        ) {
            BookmarkSetting(
                stylingState
            )
        }
    }
}