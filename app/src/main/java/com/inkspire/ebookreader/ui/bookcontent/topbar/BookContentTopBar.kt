package com.inkspire.ebookreader.ui.bookcontent.topbar

import android.app.Activity
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.common.isSuccess
import com.inkspire.ebookreader.ui.bookcontent.common.LocalCombineActions
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDataViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTableOfContentViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTopBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.composable.FindReplaceDialog
import com.inkspire.ebookreader.ui.bookcontent.composable.HighlightListItem
import com.inkspire.ebookreader.ui.bookcontent.composable.SearchResultItem
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentAction
import com.inkspire.ebookreader.ui.bookcontent.root.BookContentDataAction
import com.inkspire.ebookreader.util.ColorUtil.isDark
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookContentTopBar(
    currentChapterIndexProvider: () -> Int,
    hazeEnableProvider: () -> Boolean,
    hazeState: HazeState,
) {
    val combineActions = LocalCombineActions.current
    val drawerVM = LocalDrawerViewModel.current
    val stylingVM = LocalStylingViewModel.current
    val tocVM = LocalTableOfContentViewModel.current
    val topBarVM = LocalTopBarViewModel.current
    val dataVM = LocalDataViewModel.current

    val stylingState by stylingVM.state.collectAsStateWithLifecycle()
    val topBarState by topBarVM.state.collectAsStateWithLifecycle()
    val dataState by dataVM.state.collectAsStateWithLifecycle()

    val searchResultSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val highlightsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val tableOfContents = remember(dataState.tableOfContentState) {
        if (dataState.tableOfContentState.isSuccess) {
            (dataState.tableOfContentState as UiState.Success).data()
        } else {
            emptyList()
        }
    }

    AnimatedVisibility(
        visible = topBarState.topBarVisibility,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
    ) {
        val style = HazeMaterials.thin(stylingState.containerColor)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .then(
                    if (hazeEnableProvider()) {
                        Modifier.hazeEffect(
                            state = hazeState,
                            style = style
                        )
                    } else {
                        Modifier.background(stylingState.containerColor)
                    }
                )
                .padding(
                    PaddingValues(
                        start = WindowInsets.systemBars
                            .union(WindowInsets.displayCutout)
                            .only(WindowInsetsSides.Start)
                            .asPaddingValues()
                            .calculateStartPadding(LayoutDirection.Ltr),
                        top = WindowInsets.statusBars
                            .only(WindowInsetsSides.Top)
                            .asPaddingValues()
                            .calculateTopPadding(),
                        end = WindowInsets.systemBars
                            .union(WindowInsets.displayCutout)
                            .only(WindowInsetsSides.End)
                            .asPaddingValues()
                            .calculateEndPadding(LayoutDirection.Ltr),
                    )
                ),
        ) {
            IconButton(
                onClick = {
                    combineActions.onBackClicked()
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow),
                    contentDescription = "Back",
                    tint = stylingState.stylePreferences.textColor
                )
            }
            IconButton(
                onClick = {
                    drawerVM.onAction(DrawerAction.OpenDrawer)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_menu),
                    contentDescription = "Menu",
                    tint = stylingState.stylePreferences.textColor
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    dataVM.onAction(BookContentDataAction.LoadAllHighlights)
                    topBarVM.onAction(BookContentTopBarAction.ShowHighlightList(true))
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_text_highlight),
                    contentDescription = "Show Highlights",
                    tint = stylingState.stylePreferences.textColor
                )
            }
            if (dataState.searchResults.isNotEmpty()) {
                IconButton(
                    onClick = {
                        topBarVM.onAction(BookContentTopBarAction.ShowFindAndReplace(false))
                        topBarVM.onAction(BookContentTopBarAction.ShowSearchResultsSheet(true))
                    },
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_search_history),
                        contentDescription = "Show History",
                        tint = stylingState.stylePreferences.textColor
                    )
                }
            }
            IconButton(
                onClick = {
                    topBarVM.onAction(BookContentTopBarAction.ShowFindAndReplace(true))
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                    contentDescription = "Find and replace",
                    tint = stylingState.stylePreferences.textColor
                )
            }
            IconButton(
                onClick = {
                    tocVM.onAction(
                        TableOfContentAction.UpdateCurrentChapterFavoriteState(
                            chapterIndex = currentChapterIndexProvider(),
                            isFavorite = tableOfContents.getOrNull(currentChapterIndexProvider())?.isFavorite == true
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (tableOfContents.getOrNull(currentChapterIndexProvider())?.isFavorite == true)
                            R.drawable.ic_bookmark_filled
                        else
                            R.drawable.ic_bookmark
                    ),
                    contentDescription = "Bookmark",
                    tint = stylingState.stylePreferences.textColor
                )
            }
        }
    }

    if (topBarState.showFindAndReplace) {
        FindReplaceDialog(
            stylingState = stylingState,
            onDismiss = { topBarVM.onAction(BookContentTopBarAction.ShowFindAndReplace(false)) },
            onFind = { query, isCaseSensitive ->
                dataVM.onAction(BookContentDataAction.SearchBook(query, isCaseSensitive))
                topBarVM.onAction(BookContentTopBarAction.ShowFindAndReplace(false))
                topBarVM.onAction(BookContentTopBarAction.ShowSearchResultsSheet(true))
            },
            onReplace = { find, replace, isCaseSensitive ->
                dataVM.onAction(BookContentDataAction.FindAndReplace(find, replace, isCaseSensitive))
                topBarVM.onAction(BookContentTopBarAction.ShowFindAndReplace(false))
            }
        )
    }

    if (topBarState.showHighlightList) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = highlightsSheetState,
            onDismissRequest = { topBarVM.onAction(BookContentTopBarAction.ShowHighlightList(false)) },
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
            containerColor = stylingState.stylePreferences.backgroundColor,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 22.dp),
                    color = stylingState.stylePreferences.textColor,
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }
            }
        ) {
            val view = LocalView.current
            val isLightTheme = !stylingState.stylePreferences.backgroundColor.isDark()
            DisposableEffect(view, isLightTheme) {
                val window = (view.parent as? DialogWindowProvider)?.window
                    ?: (view.context as? Activity)?.window

                window?.let { w ->
                    val controller = WindowCompat.getInsetsController(w, view)

                    controller.isAppearanceLightStatusBars = isLightTheme
                    controller.isAppearanceLightNavigationBars = isLightTheme

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        w.isNavigationBarContrastEnforced = false
                    }
                }

                onDispose { }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Highlights",
                    style = MaterialTheme.typography.titleMedium,
                    color = stylingState.stylePreferences.textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HorizontalDivider(color = stylingState.stylePreferences.textColor)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(dataState.allHighlights) { item ->
                        HighlightListItem(
                            result = item,
                            stylingState = stylingState,
                            onClick = {
                                combineActions.navigateToParagraph(item.tocId, item.paragraphIndex)
                                topBarVM.onAction(BookContentTopBarAction.ShowHighlightList(false))
                            }
                        )
                        HorizontalDivider(
                            color = stylingState.stylePreferences.textColor.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
    if (topBarState.showSearchResultsSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = searchResultSheetState,
            onDismissRequest = { topBarVM.onAction(BookContentTopBarAction.ShowSearchResultsSheet(false)) },
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
            containerColor = stylingState.stylePreferences.backgroundColor,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 22.dp),
                    color = stylingState.stylePreferences.textColor,
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }
            }
        ) {
            val view = LocalView.current
            val isLightTheme = !stylingState.stylePreferences.backgroundColor.isDark()
            DisposableEffect(view, isLightTheme) {
                val window = (view.parent as? DialogWindowProvider)?.window
                    ?: (view.context as? Activity)?.window

                window?.let { w ->
                    val controller = WindowCompat.getInsetsController(w, view)

                    controller.isAppearanceLightStatusBars = isLightTheme
                    controller.isAppearanceLightNavigationBars = isLightTheme

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        w.isNavigationBarContrastEnforced = false
                    }
                }

                onDispose { }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "${dataState.searchResults.size} matches found",
                    style = MaterialTheme.typography.titleMedium,
                    color = stylingState.stylePreferences.textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HorizontalDivider(color = stylingState.stylePreferences.textColor)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(dataState.searchResults) { item ->
                        SearchResultItem(
                            result = item,
                            stylingState = stylingState,
                            onClick = {
                                combineActions.navigateToParagraph(item.tocId, item.paragraphIndex)
                                topBarVM.onAction(BookContentTopBarAction.ShowSearchResultsSheet(false))
                            }
                        )
                        HorizontalDivider(
                            color = stylingState.stylePreferences.textColor.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}