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
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.TableOfContent
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollState
import com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll.BottomBarAutoScroll
import com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll.BottomBarAutoScrollAction
import com.inkspire.ebookreader.ui.bookcontent.bottombar.autoscroll.BottomBarAutoScrollState
import com.inkspire.ebookreader.ui.bookcontent.bottombar.common.BottomBarMode
import com.inkspire.ebookreader.ui.bookcontent.bottombar.default.BottomBarDefault
import com.inkspire.ebookreader.ui.bookcontent.bottombar.setting.BottomBarSetting
import com.inkspire.ebookreader.ui.bookcontent.bottombar.theme.BottomBarTheme
import com.inkspire.ebookreader.ui.bookcontent.bottombar.tts.BottomBarTTS
import com.inkspire.ebookreader.ui.bookcontent.bottombar.tts.BottomBarTTSAction
import com.inkspire.ebookreader.ui.bookcontent.bottombar.tts.BottomBarTTSState
import com.inkspire.ebookreader.ui.bookcontent.chaptercontent.BookChapterContentState
import com.inkspire.ebookreader.ui.bookcontent.drawer.DrawerState
import com.inkspire.ebookreader.ui.bookcontent.styling.BookContentStylingAction
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSPlaybackState
import com.inkspire.ebookreader.ui.setting.SettingAction
import com.inkspire.ebookreader.ui.setting.SettingState
import dev.chrisbanes.haze.HazeState

@Composable
fun BookContentBottomBar(
    bookInfo: Book,
    tableOfContents: List<TableOfContent>,
    hazeState: HazeState,
    stylingState: StylingState,
    drawerState: DrawerState,
    settingState: SettingState,
    ttsPlaybackState: TTSPlaybackState,
    autoScrollState: AutoScrollState,
    bottomBarTTSState: BottomBarTTSState,
    bottomBarAutoScrollState: BottomBarAutoScrollState,
    bookChapterContentState: BookChapterContentState,
    bookContentBottomBarState: BookContentBottomBarState,
    onBookContentBottomBarAction: (BookContentBottomBarAction) -> Unit,
    onBottomBarTTSAction: (BottomBarTTSAction) -> Unit,
    onBottomBarAutoScrollAction: (BottomBarAutoScrollAction) -> Unit,
    onBottomBarSettingAction: (SettingAction) -> Unit,
    onStyleAction: (BookContentStylingAction) -> Unit
) {
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
                        stylingState = stylingState,
                        hazeState = hazeState,
                        drawerState = drawerState,
                        currentChapterTitle = tableOfContents[bookChapterContentState.currentChapterIndex].title,
                        bookInfo = bookInfo,
                        onAction = onBookContentBottomBarAction
                    )
                }
                BottomBarMode.AutoScroll -> {
                    BottomBarAutoScroll(
                        hazeState = hazeState,
                        stylingState = stylingState,
                        drawerState = drawerState,
                        autoScrollState = autoScrollState,
                        bottomBarAutoScrollState = bottomBarAutoScrollState,
                        onAction = onBottomBarAutoScrollAction
                    )
                }
                BottomBarMode.Settings -> {
                    BottomBarSetting(
                        hazeState = hazeState,
                        stylingState = stylingState,
                        drawerState = drawerState,
                        settingState = settingState,
                        ttsState = ttsPlaybackState,
                        autoScrollState = autoScrollState,
                        onSettingAction = onBottomBarSettingAction
                    )
                }
                BottomBarMode.Theme -> {
                    BottomBarTheme(
                        bookInfo = bookInfo,
                        stylingState = stylingState,
                        hazeState = hazeState,
                        drawerState = drawerState,
                        onStyleAction = onStyleAction
                    )
                }
                BottomBarMode.Tts -> {
                    BottomBarTTS(
                        stylingState = stylingState,
                        drawerState = drawerState,
                        ttsPlaybackState = ttsPlaybackState,
                        currentChapterTitle = tableOfContents[bookChapterContentState.currentChapterIndex].title,
                        hazeState = hazeState,
                        bottomBarTTSState = bottomBarTTSState,
                        onAction = onBottomBarTTSAction,
                    )
                }
            }
        }
    }
}