package com.inkspire.ebookreader.ui.bookcontent.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.LayoutDirection
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
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerAction
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentAction
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
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
                        start = WindowInsets.safeContent
                            .only(WindowInsetsSides.Start)
                            .asPaddingValues()
                            .calculateStartPadding(LayoutDirection.Ltr),
                        top = WindowInsets.statusBars
                            .only(WindowInsetsSides.Top)
                            .asPaddingValues()
                            .calculateTopPadding(),
                        end = WindowInsets.safeContent
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
}