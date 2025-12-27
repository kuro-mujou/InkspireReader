package com.inkspire.ebookreader.ui.bookcontent.drawer

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.TableOfContent
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentState
import com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark.BookmarkList
import com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark.BookmarkListAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark.BookmarkListState
import com.inkspire.ebookreader.ui.bookcontent.drawer.model.TabItem
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteList
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteState
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentScreen
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentState
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun DrawerRoot(
    bookInfo: Book,
    tableOfContents: List<TableOfContent>,
    drawerState: DrawerState,
    stylingState: StylingState,
    bookChapterContentState: BookChapterContentState,
    tableOfContentState: TableOfContentState,
    noteState: NoteState,
    bookmarkListState: BookmarkListState,
    onDrawerAction: (DrawerAction) -> Unit,
    onTableOfContentAction: (TableOfContentAction) -> Unit,
    onBookmarkListAction: (BookmarkListAction) -> Unit,
    onNoteAction: (NoteAction) -> Unit,
) {
    val tabItems = listOf(
        TabItem(title = "Table of Contents"),
        TabItem(title = "Note"),
        TabItem(title = "Book Mark"),
    )
    LaunchedEffect(drawerState.visibility) {
        onDrawerAction(DrawerAction.ChangeTabIndex(0))
    }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val imageHeight = when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT -> 140.dp
        DeviceConfiguration.PHONE_LANDSCAPE -> 80.dp
        DeviceConfiguration.TABLET_PORTRAIT -> 160.dp
        DeviceConfiguration.TABLET_LANDSCAPE -> 140.dp
    }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(
                PaddingValues(
                    start = WindowInsets.safeContent
                        .only(WindowInsetsSides.Start)
                        .asPaddingValues()
                        .calculateStartPadding(LayoutDirection.Ltr),
                    top = WindowInsets.safeContent
                        .only(WindowInsetsSides.Top)
                        .asPaddingValues()
                        .calculateTopPadding()
                )
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            AsyncImage(
                model = if (bookInfo.coverImagePath == "error") {
                    R.drawable.book_cover_not_available
                } else {
                    bookInfo.coverImagePath
                },
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(imageHeight)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = bookInfo.title,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = stylingState.textColor,
                            fontWeight = FontWeight.Medium,
                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = bookInfo.authors.joinToString(","),
                        style = TextStyle(
                            color = stylingState.textColor,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = FontWeight.Normal,
                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                        ),
                    )
                }
            }
        }
        PrimaryTabRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            selectedTabIndex = drawerState.selectedTabIndex,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        drawerState.selectedTabIndex,
                        matchContentSize = true
                    ),
                    width = Dp.Unspecified,
                    color = stylingState.textColor
                )
            },
            containerColor = Color.Transparent,
            contentColor = stylingState.textColor,
            divider = { HorizontalDivider(color = stylingState.backgroundColor) }
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == drawerState.selectedTabIndex,
                    onClick = {
                        onDrawerAction(DrawerAction.ChangeTabIndex(index))
//                        onTabItemClick()
                    },
                    modifier = Modifier.weight(1f),
                    text = {
                        Text(
                            text = item.title,
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                            )
                        )
                    },
                )
            }
        }
        Crossfade(targetState = drawerState.selectedTabIndex) { option ->
            when (option) {
                0 -> {
                    TableOfContentScreen(
                        bookChapterContentState = bookChapterContentState,
                        stylingState = stylingState,
                        tableOfContentState = tableOfContentState,
                        drawerState = drawerState,
                        tableOfContents = tableOfContents,
                        onAction = onTableOfContentAction
                    )
                }

                1 -> {
                    NoteList(
                        drawerState = drawerState,
                        noteState = noteState,
                        stylingState = stylingState,
                        onNoteAction = onNoteAction,
                        onTableOfContentAction = onTableOfContentAction
                    )
                }

                2 -> {
                    BookmarkList(
                        bookId = bookInfo.id,
                        tableOfContents = tableOfContents,
                        drawerState = drawerState,
                        stylingState = stylingState,
                        bookmarkListState = bookmarkListState,
                        onBookmarkListAction = onBookmarkListAction,
                        onTableOfContentAction = onTableOfContentAction
                    )
                }
            }
        }
    }
}