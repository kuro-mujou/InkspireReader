package com.inkspire.ebookreader.ui.bookcontent.bottombar.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.ui.bookcontent.common.LocalAutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalSettingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalTTSViewModel
import com.inkspire.ebookreader.ui.setting.SettingScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomBarSetting(
    hazeState: HazeState,
    hazeEnableProvider: () -> Boolean,
) {
    val autoScrollVM = LocalAutoScrollViewModel.current
    val ttsVM = LocalTTSViewModel.current
    val stylingVM = LocalStylingViewModel.current
    val settingVM = LocalSettingViewModel.current

    val stylingState by stylingVM.state.collectAsStateWithLifecycle()
    val settingState by settingVM.state.collectAsStateWithLifecycle()
    val ttsPlaybackState by ttsVM.state.collectAsStateWithLifecycle()
    val autoScrollState by autoScrollVM.state.collectAsStateWithLifecycle()

    val style = HazeMaterials.thin(stylingState.containerColor)
    SettingScreen(
        modifier = Modifier
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
            ),
        stylingState = stylingState,
        settingState = settingState,
        ttsState = ttsPlaybackState,
        autoScrollState = autoScrollState,
        onAction = settingVM::onAction
    )
}