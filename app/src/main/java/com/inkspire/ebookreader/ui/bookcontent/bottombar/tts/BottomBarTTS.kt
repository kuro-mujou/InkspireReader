package com.inkspire.ebookreader.ui.bookcontent.bottombar.tts

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSPlaybackState
import com.inkspire.ebookreader.ui.setting.music.MusicSetting
import com.inkspire.ebookreader.ui.setting.tts.TTSSetting
import com.inkspire.ebookreader.util.ColorUtil.isDark
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomBarTTS(
    currentChapterTitle: String,
    hazeState: HazeState,
    bottomBarTTSState: BottomBarTTSState,
    ttsPlaybackState: TTSPlaybackState,
    stylingState: StylingState,
    drawerState: DrawerState,
    onAction: (BottomBarTTSAction) -> Unit
) {
    val useHaze = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !drawerState.visibility && !drawerState.isAnimating
    val style = HazeMaterials.thin(stylingState.containerColor)
    val musicMenuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .basicMarquee(
                    animationMode = MarqueeAnimationMode.Immediately,
                    initialDelayMillis = 0,
                    repeatDelayMillis = 0
                ),
            text = currentChapterTitle,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                color = stylingState.textColor,
                textAlign = TextAlign.Center,
                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex],
            ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    onAction(BottomBarTTSAction.OnPreviousChapterClicked)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_skip_to_back),
                    tint = stylingState.textColor,
                    contentDescription = "previous chapter"
                )
            }
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    onAction(BottomBarTTSAction.OnPreviousParagraphClicked)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_backward),
                    tint = stylingState.textColor,
                    contentDescription = "previous paragraph"
                )
            }
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    onAction(BottomBarTTSAction.OnPlayPauseClicked)
                }
            ) {
                if (ttsPlaybackState.isActivated && !ttsPlaybackState.isPaused) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = "play/pause",
                        tint = stylingState.textColor
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.ic_play),
                        tint = stylingState.textColor,
                        contentDescription = "play/pause"
                    )
                }
            }
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    onAction(BottomBarTTSAction.OnNextParagraphClicked)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_forward),
                    tint = stylingState.textColor,
                    contentDescription = "next paragraph"
                )
            }
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    onAction(BottomBarTTSAction.OnNextChapterClicked)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_skip_to_next),
                    tint = stylingState.textColor,
                    contentDescription = "next chapter"
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    onAction(BottomBarTTSAction.UpdateMusicMenuVisibility)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_music_background),
                    tint = stylingState.textColor,
                    contentDescription = "background music"
                )
            }
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    onAction(BottomBarTTSAction.OnStopClicked)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_stop),
                    tint = stylingState.textColor,
                    contentDescription = "stop tts"
                )
            }
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    onAction(BottomBarTTSAction.UpdateVoiceMenuVisibility)
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_setting),
                    tint = stylingState.textColor,
                    contentDescription = "tts setting"
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }

    if (bottomBarTTSState.ttsVoiceMenuVisibility) {
        TTSSetting(
            ttsState = ttsPlaybackState,
            stylingState = stylingState,
            onDismiss = {
                onAction(BottomBarTTSAction.UpdateVoiceMenuVisibility)
            }
        )
    }

    if (bottomBarTTSState.ttsMusicMenuVisibility) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = musicMenuSheetState,
            onDismissRequest = { onAction(BottomBarTTSAction.UpdateMusicMenuVisibility) },
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
            containerColor = stylingState.backgroundColor,
            scrimColor = stylingState.containerColor,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 22.dp),
                    color = stylingState.textColor,
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }
            }
        ) {
            val view = LocalView.current
            val isLightTheme = !stylingState.backgroundColor.isDark()

            DisposableEffect(view, isLightTheme) {
                val window = (view.parent as? DialogWindowProvider)?.window
                    ?: (view.context as? Activity)?.window

                window?.let { w ->
                    val controller = WindowCompat.getInsetsController(w, view)

                    controller.isAppearanceLightStatusBars = isLightTheme
                    controller.isAppearanceLightNavigationBars = isLightTheme

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        w.isNavigationBarContrastEnforced = false
                    }
                }

                onDispose { }
            }
            MusicSetting(
                stylingState = stylingState,
            )
        }
    }
}