package com.inkspire.ebookreader.ui.setting

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.setting.autoscroll.AutoScrollSetting
import com.inkspire.ebookreader.ui.setting.bookcategory.BookCategorySetting
import com.inkspire.ebookreader.ui.setting.bookmark.BookmarkSetting
import com.inkspire.ebookreader.ui.setting.music.MusicSetting
import com.inkspire.ebookreader.ui.setting.tts.TTSSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    settingState: SettingState,
    onAction: (SettingAction) -> Unit,
) {
    val musicMenuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bookmarkMenuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categoryMenuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (settingState.openTTSVoiceMenu) {
        TTSSetting(
            onDismiss = {
                onAction(SettingAction.OpenTTSVoiceMenu(false))
            },
        )
    }
    if (settingState.openAutoScrollMenu) {
        AutoScrollSetting(
            onDismissRequest = {
                onAction(SettingAction.OpenAutoScrollMenu(false))
            }
        )
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp),
            text = "Setting",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        HorizontalDivider(thickness = 2.dp)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        onAction(SettingAction.KeepScreenOn(!settingState.keepScreenOn))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_keep_screen_on),
                    contentDescription = "keep screen on"
                )
                Text(
                    text = "Keep Screen On",
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = settingState.keepScreenOn,
                    onCheckedChange = {
                        onAction(SettingAction.KeepScreenOn(it))
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            HorizontalDivider(thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        onAction(SettingAction.OpenBackgroundMusicMenu(true))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_music_background),
                    contentDescription = "background music"
                )
                Text(
                    text = "Background Music",
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(30.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
            HorizontalDivider(thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        onAction(SettingAction.OpenBookmarkThemeMenu(true))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_tag),
                    contentDescription = "Bookmark theme"
                )
                Text(
                    text = "Bookmark theme",
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(30.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
            HorizontalDivider(thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        onAction(SettingAction.OpenTTSVoiceMenu(true))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_headphones),
                    contentDescription = "text to speech"
                )
                Text(
                    text = "Text to Speech",
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(30.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
            HorizontalDivider(thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        onAction(SettingAction.OpenAutoScrollMenu(true))
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_scroll),
                    contentDescription = "auto scroll up"
                )
                Text(
                    text = "Auto Scroll Up",
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(30.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
            HorizontalDivider(thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        onAction(SettingAction.OpenCategoryMenu(true))
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_grid_view),
                    contentDescription = "book category"
                )
                Text(
                    text = "Book Category",
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(30.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
        }
    }
    if (settingState.openBackgroundMusicMenu) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = musicMenuSheetState,
            onDismissRequest = { onAction(SettingAction.OpenBackgroundMusicMenu(false)) },
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
        ) {
            val view = LocalView.current
            SideEffect {
                val window = (view.context as? Activity)?.window ?: (view.parent as? DialogWindowProvider)?.window
                window?.let {
                    if (Build.VERSION.SDK_INT >= 29) {
                        it.isNavigationBarContrastEnforced = false
                    }
                }
            }
            MusicSetting()
        }
    }
    if (settingState.openBookmarkThemeMenu) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = bookmarkMenuSheetState,
            onDismissRequest = { onAction(SettingAction.OpenBookmarkThemeMenu(false)) },
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
        ) {
            val view = LocalView.current
            SideEffect {
                val window = (view.context as? Activity)?.window ?: (view.parent as? DialogWindowProvider)?.window
                window?.let {
                    if (Build.VERSION.SDK_INT >= 29) {
                        it.isNavigationBarContrastEnforced = false
                    }
                }
            }
            BookmarkSetting()
        }
    }
    if (settingState.openCategoryMenu) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = categoryMenuSheetState,
            onDismissRequest = {
                onAction(SettingAction.OpenCategoryMenu(false))
            },
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
        ) {
            val view = LocalView.current
            SideEffect {
                val window = (view.context as? Activity)?.window ?: (view.parent as? DialogWindowProvider)?.window
                window?.let {
                    if (Build.VERSION.SDK_INT >= 29) {
                        it.isNavigationBarContrastEnforced = false
                    }
                }
            }
            BookCategorySetting()
        }
    }
}