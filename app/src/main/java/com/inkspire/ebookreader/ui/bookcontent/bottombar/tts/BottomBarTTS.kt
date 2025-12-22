package com.inkspire.ebookreader.ui.bookcontent.bottombar.tts

import android.os.Build
import androidx.compose.runtime.Composable
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun BottomBarTTS(
    stylingState: StylingState,
    drawerState: DrawerState,
) {
    val useHaze = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !drawerState.visibility && !drawerState.isAnimating

}