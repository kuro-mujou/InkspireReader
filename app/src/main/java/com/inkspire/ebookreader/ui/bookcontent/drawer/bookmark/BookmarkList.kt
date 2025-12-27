package com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark

import android.util.Log
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.domain.model.TableOfContent
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentAction
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.setting.bookmark.BookmarkSetting
import com.inkspire.ebookreader.ui.setting.bookmark.composable.MyBookmarkCard

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun BookmarkList(
    bookId: String,
    tableOfContents: List<TableOfContent>,
    drawerState: DrawerState,
    stylingState: StylingState,
    bookmarkListState: BookmarkListState,
    onBookmarkListAction: (BookmarkListAction) -> Unit,
    onTableOfContentAction: (TableOfContentAction) -> Unit
) {
    val bookmarkMenuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

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
                    onBookmarkListAction(BookmarkListAction.UpdateBookmarkThemeSettingVisibility)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_setting),
                    contentDescription = null,
                    tint = stylingState.textColor
                )
            }
            AnimatedVisibility(
                visible = bookmarkListState.enableUndoDeleteBookmark
            ) {
                IconButton(
                    onClick = {
                        onBookmarkListAction(BookmarkListAction.UndoDeleteBookmark(bookId))
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
                        contentDescription = null,
                        tint = stylingState.textColor
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
                items = tableOfContents.filter { it.isFavorite },
                key = { it.index }
            ) { bookMark ->
                MyBookmarkCard(
                    bookmarkContent = bookMark.title,
                    bookmarkIndex = bookMark.index,
                    bookmarkStyle = bookmarkListState.selectedBookmarkStyle,
                    deletable = true,
                    stylingState = stylingState,
                    onCardClicked = {
                        Log.d("BookmarkList", "onCardClicked: ${bookMark.index}")
                        onTableOfContentAction(TableOfContentAction.NavigateToChapter(bookMark.index))
                    },
                    onDeleted = {
                        onBookmarkListAction(BookmarkListAction.DeleteBookmark(bookId, bookMark.index))
                    }
                )
            }
        }
    }
    if (bookmarkListState.bookmarkThemeSettingVisibility) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            dragHandle = {
                Surface(
                    modifier = Modifier
                        .padding(vertical = 22.dp),
                    color = stylingState.textColor,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }
            },
            sheetState = bookmarkMenuSheetState,
            onDismissRequest = {
                onBookmarkListAction(BookmarkListAction.UpdateBookmarkThemeSettingVisibility)
            },
            containerColor = stylingState.backgroundColor
        ) {
            BookmarkSetting(
                stylingState
            )
        }
    }
}