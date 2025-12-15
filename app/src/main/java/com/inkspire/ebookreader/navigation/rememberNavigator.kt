package com.inkspire.ebookreader.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration

@Composable
fun rememberNavigator(
    config: SavedStateConfiguration,
    startDestination: NavKey
): Navigator {
    val navBackStack = rememberNavBackStack(config, startDestination)

    return remember(navBackStack) {
        Navigator(navBackStack, startDestination)
    }
}