package com.inkspire.ebookreader.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.inkspire.ebookreader.navigation.Navigator
import com.inkspire.ebookreader.common.DeviceConfiguration

@Composable
fun MyNavigationSuiteScaffold(
    navigator: Navigator,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

        when (deviceConfiguration) {
            DeviceConfiguration.PHONE_PORTRAIT,
            DeviceConfiguration.TABLET_PORTRAIT -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        content(paddingValues)
                    }
                    MyBottomNavigation(navigator)
                }
            }

            DeviceConfiguration.PHONE_LANDSCAPE,
            DeviceConfiguration.TABLET_LANDSCAPE -> {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MyNavigationRail(navigator)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        content(paddingValues)
                    }
                }
            }
        }
    }
}

