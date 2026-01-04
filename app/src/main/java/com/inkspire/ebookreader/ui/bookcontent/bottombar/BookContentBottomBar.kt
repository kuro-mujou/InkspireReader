package com.inkspire.ebookreader.ui.bookcontent.bottombar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll.BottomBarAutoScroll
import com.inkspire.ebookreader.ui.bookcontent.bottombar.common.BottomBarMode
import com.inkspire.ebookreader.ui.bookcontent.bottombar.default.BottomBarDefault
import com.inkspire.ebookreader.ui.bookcontent.bottombar.setting.BottomBarSetting
import com.inkspire.ebookreader.ui.bookcontent.bottombar.theme.BottomBarTheme
import com.inkspire.ebookreader.ui.bookcontent.bottombar.tts.BottomBarTTS
import com.inkspire.ebookreader.ui.bookcontent.common.LocalBottomBarViewModel
import dev.chrisbanes.haze.HazeState

@Composable
fun BookContentBottomBar(
    hazeState: HazeState,
    hazeEnableProvider: () -> Boolean,
    bookInfoProvider: () -> Book,
    chapterTitleProvider: () -> String,
    firstVisibleIndexProvider: () -> Int
) {
    val bottomBarVM = LocalBottomBarViewModel.current
    val bookContentBottomBarState by bottomBarVM.state.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = bookContentBottomBarState.bottomBarVisibility,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        AnimatedContent(
            targetState = bookContentBottomBarState.bottomBarMode,
            transitionSpec = {
                (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                        scaleIn(initialScale = 0.9f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow)))
                    .togetherWith(
                        fadeOut(animationSpec = tween(100))
                    )
            },
            label = "BottomBarModeSwap"
        ) { targetState ->
            when(targetState){
                BottomBarMode.Main -> {
                    BottomBarDefault(
                        hazeState = hazeState,
                        hazeEnableProvider = hazeEnableProvider,
                        chapterTitleProvider = chapterTitleProvider,
                        bookInfoProvider = bookInfoProvider,
                        firstVisibleIndexProvider = firstVisibleIndexProvider
                    )
                }
                BottomBarMode.AutoScroll -> {
                    BottomBarAutoScroll(
                        hazeState = hazeState,
                        hazeEnableProvider = hazeEnableProvider,
                    )
                }
                BottomBarMode.Settings -> {
                    BottomBarSetting(
                        hazeState = hazeState,
                        hazeEnableProvider = hazeEnableProvider,
                    )
                }
                BottomBarMode.Theme -> {
                    BottomBarTheme(
                        hazeState = hazeState,
                        hazeEnableProvider = hazeEnableProvider,
                        bookInfoProvider = bookInfoProvider,
                    )
                }
                BottomBarMode.Tts -> {
                    BottomBarTTS(
                        hazeState = hazeState,
                        hazeEnableProvider = hazeEnableProvider,
                        chapterTitleProvider = chapterTitleProvider,
                    )
                }
            }
        }
    }
}