package com.inkspire.ebookreader.ui.bookcontent.common

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat


@Composable
fun rememberInsetsController(): WindowInsetsControllerCompat? {
    val window = LocalActivity.current?.window ?: return null
    val view = LocalView.current
    return remember(window, view) {
        WindowInsetsControllerCompat(window, view)
    }
}