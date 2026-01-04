package com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.common.isSuccess
import com.inkspire.ebookreader.ui.bookcontent.common.LocalChapterContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalCombineActions
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDataViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTableOfContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.composable.CustomNavigationDrawerItem
import com.inkspire.ebookreader.ui.composable.MySearchBox
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TableOfContentScreen() {
    val combineActions = LocalCombineActions.current
    val chapterContentVM = LocalChapterContentViewModel.current
    val drawerVM = LocalDrawerViewModel.current
    val stylingVM = LocalStylingViewModel.current
    val tocVM = LocalTableOfContentViewModel.current
    val dataVM = LocalDataViewModel.current

    val drawerState by drawerVM.state.collectAsStateWithLifecycle()
    val stylingState by stylingVM.state.collectAsStateWithLifecycle()
    val tableOfContentState by tocVM.state.collectAsStateWithLifecycle()
    val bookChapterContentState by chapterContentVM.state.collectAsStateWithLifecycle()
    val tocState by remember(dataVM) {
        dataVM.state.map { it.tableOfContentState }.distinctUntilChanged()
    }.collectAsStateWithLifecycle(initialValue = UiState.None)

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible
    val lazyColumnState = rememberLazyListState()
    val searchState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    val tableOfContents = remember(tocState) {
        if (tocState.isSuccess) {
            (tocState as UiState.Success).data()
        } else {
            emptyList()
        }
    }
    val tableOfContentSize by rememberUpdatedState(tableOfContents.size)

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }
    LaunchedEffect(tableOfContentState.searchState) {
        if (tableOfContentState.searchState) {
            lazyColumnState.scrollToItem(tableOfContentState.targetSearchIndex)
            tocVM.onAction(TableOfContentAction.UpdateSearchState(false))
        }
    }
    LaunchedEffect(drawerState.visibility) {
        if (drawerState.visibility) {
            lazyColumnState.scrollToItem(bookChapterContentState.currentChapterIndex)
        } else {
            searchState.clearText()
            tocVM.onAction(TableOfContentAction.UpdateTargetSearchIndex(-1))
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
            MySearchBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textFieldState = searchState,
                textColor = stylingState.stylePreferences.textColor,
                backgroundColor = stylingState.containerColor,
                cursorColor = stylingState.stylePreferences.textColor,
                textStyle = TextStyle(
                    color = stylingState.stylePreferences.textColor,
                    fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                ),
                hint = {
                    Text(
                        text = "Enter a chapter number",
                        style = TextStyle(
                            color = stylingState.stylePreferences.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = {
                    val chapterIndex = searchState.text.toString().toIntOrNull()
                    if (chapterIndex != null) {
                        tocVM.onAction(
                            TableOfContentAction.UpdateTargetSearchIndex(
                                if (chapterIndex < tableOfContentSize)
                                    chapterIndex
                                else
                                    tableOfContentSize - 1
                            )
                        )
                        tocVM.onAction(
                            TableOfContentAction.UpdateSearchState(true)
                        )
                        focusManager.clearFocus()
                    }
                }
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
                                            color = stylingState.stylePreferences.textColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                                        )
                                    }
                                    bookChapterContentState.currentChapterIndex -> {
                                        TextStyle(
                                            color = stylingState.containerColor,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                                        )
                                    }
                                    else -> {
                                        TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
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
                                combineActions.navigateToChapter(index)
                            }
                            .then(
                                if (index == tableOfContentState.targetSearchIndex)
                                    Modifier.border(
                                        width = 1.dp,
                                        color = stylingState.stylePreferences.textColor,
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
                                    stylingState.stylePreferences.textColor
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
//            LaunchedEffect(lazyColumnState) {
//                snapshotFlow { lazyColumnState.layoutInfo.visibleItemsInfo.firstOrNull()?.index }
//                    .collect { index ->
//                        tocVM.onAction(TableOfContentAction.UpdateFirstVisibleTocIndex(index ?: 0))
//                    }
//            }
//            LaunchedEffect(lazyColumnState) {
//                snapshotFlow { lazyColumnState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
//                    .collect { index ->
//                        tocVM.onAction(TableOfContentAction.UpdateLastVisibleTocIndex(index ?: 0))
//                    }
//            }
//            LaunchedEffect(
//                tableOfContentState.firstVisibleTocIndex,
//                tableOfContentState.lastVisibleTocIndex
//            ) {
//                tocVM.onAction(
//                    TableOfContentAction.ChangeFabVisibility(
//                        bookChapterContentState.currentChapterIndex !in
//                            tableOfContentState.firstVisibleTocIndex..tableOfContentState.lastVisibleTocIndex
//                    )
//                )
//            }
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
                    containerColor = stylingState.stylePreferences.textColor,
                )
            ) {
                if (bookChapterContentState.currentChapterIndex < tableOfContentState.firstVisibleTocIndex)
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_up),
                        modifier = Modifier.size(16.dp),
                        contentDescription = null,
                        tint = stylingState.stylePreferences.backgroundColor,
                    )
                else
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_down),
                        modifier = Modifier.size(16.dp),
                        contentDescription = null,
                        tint = stylingState.stylePreferences.backgroundColor,
                    )
            }
        }
    }
}