package com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.domain.model.TableOfContent
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentState
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.composable.CustomNavigationDrawerItem
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import kotlinx.coroutines.launch

@Composable
fun TableOfContentScreen(
    bookChapterContentState: BookChapterContentState,
    stylingState: StylingState,
    tableOfContentState: TableOfContentState,
    drawerState: DrawerState,
    tableOfContents: List<TableOfContent>,
    onAction: (TableOfContentAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var searchInput by remember { mutableStateOf("") }
    val lazyColumnState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(tableOfContentState.searchState) {
        if (tableOfContentState.searchState) {
            lazyColumnState.scrollToItem(tableOfContentState.targetSearchIndex)
            onAction(TableOfContentAction.UpdateSearchState(false))
        }
    }
    LaunchedEffect(drawerState.visibility) {
        if (drawerState.visibility) {
            lazyColumnState.scrollToItem(bookChapterContentState.currentChapterIndex)
        } else {
            searchInput = ""
            onAction(TableOfContentAction.UpdateTargetSearchIndex(-1))
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }
    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            OutlinedTextField(
                value = searchInput,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        searchInput = newValue
                    }
                },
                textStyle = TextStyle(
                    color = stylingState.textColor,
                    fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                ),
                label = {
                    Text(
                        text = "Enter a chapter number",
                        style = TextStyle(
                            color = stylingState.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        val chapterIndex = searchInput.toIntOrNull()
                        if (chapterIndex != null) {
                            onAction(
                                TableOfContentAction.UpdateTargetSearchIndex(
                                    if (chapterIndex < tableOfContents.size)
                                        chapterIndex
                                    else
                                        tableOfContents.size - 1
                                )
                            )
                            onAction(
                                TableOfContentAction.UpdateSearchState(true)
                            )
                            focusManager.clearFocus()
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = stylingState.textColor,
                    unfocusedLabelColor = stylingState.textColor,
                    focusedBorderColor = stylingState.textColor,
                    focusedLabelColor = stylingState.textColor,
                    cursorColor = stylingState.textColor,
                )
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = lazyColumnState,
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
            ) {
                itemsIndexed(
                    items = tableOfContents,
                    key = { _, tocItem -> tocItem.index }
                ) { index, tocItem ->
                    CustomNavigationDrawerItem(
                        label = {
                            Text(
                                text = tocItem.title,
                                style = when (index) {
                                    tableOfContentState.targetSearchIndex -> {
                                        TextStyle(
                                            color = stylingState.textColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                                        )
                                    }
                                    bookChapterContentState.currentChapterIndex -> {
                                        TextStyle(
                                            color = stylingState.containerColor,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                                        )
                                    }
                                    else -> {
                                        TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
                                        )
                                    }
                                },
                            )
                        },
                        selected = index == bookChapterContentState.currentChapterIndex,
                        modifier = Modifier
                            .padding(4.dp, 2.dp, 4.dp, 2.dp)
                            .wrapContentHeight()
                            .clickable {
                                onAction(TableOfContentAction.NavigateToChapter(index))
                            }
                            .then(
                                if (index == tableOfContentState.targetSearchIndex)
                                    Modifier.border(
                                        width = 1.dp,
                                        color = stylingState.textColor,
                                        shape = RoundedCornerShape(25.dp)
                                    )
                                else {
                                    Modifier
                                }
                            ),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor =
                                if (index == tableOfContentState.targetSearchIndex) {
                                    stylingState.textBackgroundColor
                                } else {
                                    stylingState.textColor
                                },
                            unselectedContainerColor =
                                if (index == tableOfContentState.targetSearchIndex) {
                                    stylingState.textBackgroundColor
                                } else {
                                    Color.Transparent
                                },
                            selectedTextColor = stylingState.tocTextColor,
                            unselectedTextColor = stylingState.tocTextColor.copy(alpha = 0.75f),
                        )
                    )
                }
            }
            LaunchedEffect(lazyColumnState) {
                snapshotFlow { lazyColumnState.layoutInfo.visibleItemsInfo.firstOrNull()?.index }
                    .collect { index ->
                        onAction(TableOfContentAction.UpdateFirstVisibleTocIndex(index ?: 0))
                    }
            }
            LaunchedEffect(lazyColumnState) {
                snapshotFlow { lazyColumnState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { index ->
                        onAction(TableOfContentAction.UpdateLastVisibleTocIndex(index ?: 0))
                    }
            }
            LaunchedEffect(
                tableOfContentState.firstVisibleTocIndex,
                tableOfContentState.lastVisibleTocIndex
            ) {
                onAction(
                    TableOfContentAction.ChangeFabVisibility(
                        bookChapterContentState.currentChapterIndex !in
                            tableOfContentState.firstVisibleTocIndex..tableOfContentState.lastVisibleTocIndex
                    )
                )
            }
        }
        if (tableOfContentState.fabVisibility) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        bottom = 4.dp + WindowInsets.navigationBars
                            .only(WindowInsetsSides.Bottom)
                            .asPaddingValues()
                            .calculateBottomPadding(),
                        end = 4.dp),
                onClick = {
                    scope.launch {
                        lazyColumnState.animateScrollToItem(bookChapterContentState.currentChapterIndex)
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = stylingState.textColor,
                )
            ) {
                if (bookChapterContentState.currentChapterIndex < tableOfContentState.firstVisibleTocIndex)
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_up),
                        modifier = Modifier.size(16.dp),
                        contentDescription = null,
                        tint = stylingState.backgroundColor,
                    )
                else
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_down),
                        modifier = Modifier.size(16.dp),
                        contentDescription = null,
                        tint = stylingState.backgroundColor,
                    )
            }
        }
    }
}