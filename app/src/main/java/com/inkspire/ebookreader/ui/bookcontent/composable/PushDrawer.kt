package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.rememberInsetsController
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerAction
import com.inkspire.ebookreader.util.ColorUtil.isDark

@Composable
fun PushDrawer(
    drawerContent: @Composable BoxScope.() -> Unit,
    mainContent: @Composable BoxScope.() -> Unit
) {
    val drawerVM = LocalDrawerViewModel.current
    val stylingVM = LocalStylingViewModel.current

    val drawerState by drawerVM.state.collectAsStateWithLifecycle()
    val stylingState by stylingVM.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val drawerWidth = when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT -> 300.dp
        DeviceConfiguration.PHONE_LANDSCAPE -> 360.dp
        DeviceConfiguration.TABLET_PORTRAIT -> 360.dp
        DeviceConfiguration.TABLET_LANDSCAPE -> 480.dp
    }
    val padding = when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT -> 32.dp
        DeviceConfiguration.PHONE_LANDSCAPE -> 64.dp
        DeviceConfiguration.TABLET_PORTRAIT -> 64.dp
        DeviceConfiguration.TABLET_LANDSCAPE -> 128.dp
    }
    val mainContentScale = 0.85f
    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.visibility) (drawerWidth + padding) else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "MainContentOffset",
        finishedListener = {
            drawerVM.onAction(DrawerAction.UpdateDrawerAnimateState(false))
        }
    )
    val animatedElevation by animateDpAsState(
        targetValue = if (drawerState.visibility) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "MainContentElevation"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.visibility) mainContentScale else 1.0f,
        animationSpec = tween(durationMillis = 300),
        label = "MainContentScale"
    )
    val animatedCornerRadius by animateDpAsState(
        targetValue = if (drawerState.visibility) 24.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "MainContentCornerRadius"
    )
    val animatedRotation by animateFloatAsState(
        targetValue = if (drawerState.visibility) -15f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "MainContentRotation"
    )
    val insetsController = rememberInsetsController()
    val shouldUseDarkIcons = !stylingState.drawerContainerColor.isDark()

    SideEffect {
        insetsController?.isAppearanceLightStatusBars = shouldUseDarkIcons
        insetsController?.isAppearanceLightNavigationBars = shouldUseDarkIcons
    }
    LaunchedEffect(drawerState.visibility) {
        if (drawerState.fromUser) {
            drawerVM.onAction(DrawerAction.UpdateDrawerAnimateState(true))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(stylingState.drawerContainerColor)
    ) {
        Box(
            modifier = Modifier
                .width(drawerWidth)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = drawerState.visibility,
                    onClick = {
                        drawerVM.onAction(DrawerAction.CloseDrawer)
                    }
                )
        ) {
            drawerContent()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(x = animatedOffset.roundToPx(), y = 0) }
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                    rotationY = animatedRotation
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                    cameraDistance = 12f * density
                    shadowElevation = animatedElevation.toPx()
                    shape = RoundedCornerShape(animatedCornerRadius)
                    clip = animatedCornerRadius > 0.dp
                }
        ) {
            mainContent()
            if (drawerState.visibility)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                drawerVM.onAction(DrawerAction.CloseDrawer)
                            }
                        )
                )
        }
    }
}