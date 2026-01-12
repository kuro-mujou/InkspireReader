package com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.common.LocalAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalBottomBarAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalCombineActions
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.setting.autoscroll.AutoScrollSetting
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomBarAutoScroll(
    hazeEnableProvider: () -> Boolean,
    hazeState: HazeState,
) {
    val combineActions = LocalCombineActions.current
    val autoScrollVM = LocalAutoScrollViewModel.current
    val bottomBarAutoScrollVM = LocalBottomBarAutoScrollViewModel.current
    val stylingVM = LocalStylingViewModel.current
    
    val stylingState by stylingVM.state.collectAsStateWithLifecycle()
    val autoScrollState by autoScrollVM.state.collectAsStateWithLifecycle()
    val bottomBarAutoScrollState by bottomBarAutoScrollVM.state.collectAsStateWithLifecycle()
    
    val style = HazeMaterials.thin(stylingState.containerColor)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                PaddingValues(
                    start = WindowInsets.systemBars
                        .union(WindowInsets.displayCutout)
                        .only(WindowInsetsSides.Start)
                        .asPaddingValues()
                        .calculateStartPadding(LayoutDirection.Ltr),
                    end = WindowInsets.systemBars
                        .union(WindowInsets.displayCutout)
                        .only(WindowInsetsSides.End)
                        .asPaddingValues()
                        .calculateEndPadding(LayoutDirection.Ltr),
                    bottom = WindowInsets.navigationBars
                        .only(WindowInsetsSides.Bottom)
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
            )
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(CircleShape)
                .then(
                    if (hazeEnableProvider()) {
                        Modifier.hazeEffect(
                            state = hazeState,
                            style = style
                        )
                    } else {
                        Modifier.background(
                            stylingState.containerColor
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    modifier = Modifier
                        .size(50.dp),
                    onClick = {
                        combineActions.onStopAutoScroll()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.ic_stop),
                        tint = stylingState.stylePreferences.textColor,
                        contentDescription = "stop"
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    modifier = Modifier
                        .size(50.dp),
                    onClick = {
                        combineActions.onToggleAutoScroll(!autoScrollState.isPaused)
                    }
                ) {
                    if (autoScrollState.isPaused) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.ic_play),
                            tint = stylingState.stylePreferences.textColor,
                            contentDescription = "play/pause"
                        )
                    } else {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.ic_pause),
                            tint = stylingState.stylePreferences.textColor,
                            contentDescription = "play/pause"
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    modifier = Modifier
                        .size(50.dp),
                    onClick = {
                        bottomBarAutoScrollVM.onAction(BottomBarAutoScrollAction.ChangeSettingVisibility)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.ic_setting),
                        tint = stylingState.stylePreferences.textColor,
                        contentDescription = "setting"
                    )
                }
            }
            if (bottomBarAutoScrollState.settingVisibility) {
                AutoScrollSetting(
                    stylingState = stylingState,
                    autoScrollState = autoScrollState,
                    onDismissRequest = {
                        bottomBarAutoScrollVM.onAction(BottomBarAutoScrollAction.ChangeSettingVisibility)
                    }
                )
            }
        }
    }
}