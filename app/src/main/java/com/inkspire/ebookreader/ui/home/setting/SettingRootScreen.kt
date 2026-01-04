package com.inkspire.ebookreader.ui.home.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollViewModel
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSViewModel
import com.inkspire.ebookreader.ui.setting.SettingScreen
import com.inkspire.ebookreader.ui.setting.SettingViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SettingRootScreen() {
    val settingViewModel = koinViewModel<SettingViewModel>()
    val autoScrollViewModel = koinViewModel<AutoScrollViewModel>()
    val ttsViewModel = koinViewModel<TTSViewModel>(parameters = { parametersOf(false) })
    val settingState by settingViewModel.state.collectAsStateWithLifecycle()
    val autoScrollState by autoScrollViewModel.state.collectAsStateWithLifecycle()
    val ttsState by ttsViewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.safeContent
                        .only(WindowInsetsSides.Top)
                        .asPaddingValues()
                        .calculateTopPadding(),
                    end = WindowInsets.safeContent
                        .only(WindowInsetsSides.End)
                        .asPaddingValues()
                        .calculateRightPadding(LayoutDirection.Ltr)
                ),
            ttsState = ttsState,
            autoScrollState = autoScrollState,
            settingState = settingState,
            onAction = settingViewModel::onAction
        )
    }
}