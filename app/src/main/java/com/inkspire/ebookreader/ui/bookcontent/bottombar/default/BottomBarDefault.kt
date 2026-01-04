package com.inkspire.ebookreader.ui.bookcontent.bottombar.default

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.ui.bookcontent.bottombar.BookContentBottomBarAction
import com.inkspire.ebookreader.ui.bookcontent.common.LocalBottomBarViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalCombineActions
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomBarDefault(
    hazeState: HazeState,
    hazeEnableProvider: () -> Boolean,
    chapterTitleProvider: () -> String,
    bookInfoProvider: () -> Book,
    firstVisibleIndexProvider: () -> Int
) {
    val combineActions = LocalCombineActions.current
    val bottomBarVM = LocalBottomBarViewModel.current
    val stylingVM = LocalStylingViewModel.current

    val stylingState by stylingVM.state.collectAsStateWithLifecycle()

    val style = HazeMaterials.thin(stylingState.containerColor)
    Column(
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
                    end = WindowInsets.safeContent
                        .only(WindowInsetsSides.End)
                        .asPaddingValues()
                        .calculateEndPadding(LayoutDirection.Ltr),
                    bottom = WindowInsets.navigationBars
                        .only(WindowInsetsSides.Bottom)
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .basicMarquee(
                    animationMode = MarqueeAnimationMode.Immediately,
                    initialDelayMillis = 0,
                    repeatDelayMillis = 0
                ),
            text = chapterTitleProvider(),
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                color = stylingState.stylePreferences.textColor,
                textAlign = TextAlign.Center,
                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
            ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    bottomBarVM.onAction(BookContentBottomBarAction.ThemeIconClicked)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_theme),
                    tint = stylingState.stylePreferences.textColor,
                    contentDescription = "theme"
                )
            }
            if (bookInfoProvider().fileType != "cbz" && bookInfoProvider().fileType != "pdf/images") {
                IconButton(
                    modifier = Modifier.size(50.dp),
                    onClick = {
                        combineActions.onTTSActivated(firstVisibleIndexProvider())
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_headphones),
                        tint = stylingState.stylePreferences.textColor,
                        contentDescription = "start tts"
                    )
                }
            }
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    combineActions.onAutoScrollActivated()
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_scroll),
                    tint = stylingState.stylePreferences.textColor,
                    contentDescription = "auto scroll"
                )
            }

            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    bottomBarVM.onAction(BookContentBottomBarAction.SettingIconClicked)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_setting),
                    tint = stylingState.stylePreferences.textColor,
                    contentDescription = "setting"
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}