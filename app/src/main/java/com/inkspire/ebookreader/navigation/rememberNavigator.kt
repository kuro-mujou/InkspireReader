package com.inkspire.ebookreader.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

@Composable
fun rememberNavigator(
    startDestination: NavKey
): Navigator {
    val navBackStack = rememberNavBackStack(startDestination)
    return remember(navBackStack) {
        Navigator(navBackStack, startDestination)
    }
}