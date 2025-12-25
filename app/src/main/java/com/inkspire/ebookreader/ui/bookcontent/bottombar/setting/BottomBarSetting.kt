package com.inkspire.ebookreader.ui.bookcontent.bottombar.setting

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollState
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSPlaybackState
import com.inkspire.ebookreader.ui.setting.SettingAction
import com.inkspire.ebookreader.ui.setting.SettingScreen
import com.inkspire.ebookreader.ui.setting.SettingState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomBarSetting(
    hazeState: HazeState,
    stylingState: StylingState,
    settingState: SettingState,
    ttsState: TTSPlaybackState,
    autoScrollState: AutoScrollState,
    drawerState: DrawerState,
    onSettingAction: (SettingAction) -> Unit,
) {
    val useHaze = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !drawerState.visibility && !drawerState.isAnimating
    val style = HazeMaterials.thin(stylingState.containerColor)
    SettingScreen(
        modifier = Modifier
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
        stylingState = stylingState,
        settingState = settingState,
        ttsState = ttsState,
        autoScrollState = autoScrollState,
        onAction = onSettingAction
    )
}