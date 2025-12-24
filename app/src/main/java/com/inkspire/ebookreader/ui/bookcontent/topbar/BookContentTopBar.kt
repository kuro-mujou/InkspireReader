package com.inkspire.ebookreader.ui.bookcontent.topbar

import android.os.Build
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.LayoutDirection
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.domain.model.TableOfContent
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentState
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BookContentTopBar(
    tableOfContents: List<TableOfContent>,
    hazeState: HazeState,
    stylingState: StylingState,
    drawerState: DrawerState,
    bookChapterContentState: BookChapterContentState,
    bookContentTopBarState: BookContentTopBarState,
    onAction: (BookContentTopBarAction) -> Unit
) {
    AnimatedVisibility(
        visible = bookContentTopBarState.topBarVisibility,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
    ) {
        val style = HazeMaterials.thin(stylingState.containerColor)
        val useHaze = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !drawerState.visibility && !drawerState.isAnimating
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .then(
                    if (useHaze) {
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
                    onAction(BookContentTopBarAction.BackIconClicked)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow),
                    contentDescription = "Back",
                    tint = stylingState.textColor
                )
            }
            IconButton(
                onClick = {
                    onAction(BookContentTopBarAction.DrawerIconClicked)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_menu),
                    contentDescription = "Menu",
                    tint = stylingState.textColor
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    onAction(BookContentTopBarAction.BookmarkIconClicked)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (tableOfContents[bookChapterContentState.currentChapterIndex].isFavorite)
                            R.drawable.ic_bookmark_filled
                        else
                            R.drawable.ic_bookmark
                    ),
                    contentDescription = "Bookmark",
                    tint = stylingState.textColor
                )
            }
        }
    }
}