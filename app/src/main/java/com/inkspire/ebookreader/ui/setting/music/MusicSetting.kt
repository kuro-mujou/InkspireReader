package com.inkspire.ebookreader.ui.setting.music

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.setting.music.composable.MyMusicItemView
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSetting(
    stylingState: StylingState? = null,
) {
    val viewModel = koinViewModel<MusicSettingViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onAction(MusicSettingAction.OnAddPerform(it, context))
        }
    }
    var volumeSliderValue by remember { mutableFloatStateOf(state.playerVolume) }
    Surface(
        color = stylingState?.backgroundColor ?: MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets.safeContent
                        .only(WindowInsetsSides.Horizontal)
                        .asPaddingValues()
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center),
                    text = "MUSIC MENU",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                    )
                )
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        launcher.launch(arrayOf("audio/mpeg", "audio/wav"))
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_add_music),
                        contentDescription = null,
                        tint = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Enable background music",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = state.enableBackgroundMusic,
                    onCheckedChange = {
                        viewModel.onAction(MusicSettingAction.OnEnableBackgroundMusicChange(it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface,
                        checkedTrackColor = stylingState?.textColor?.copy(0.5f) ?: MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        checkedBorderColor = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface,
                        uncheckedThumbColor = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface,
                        uncheckedTrackColor = stylingState?.textColor?.copy(0.5f) ?: MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        uncheckedBorderColor = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Volume",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                    )
                )
                Text(
                    text = "%.2fx".format(volumeSliderValue),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                    )
                )
            }
            Slider(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                value = volumeSliderValue,
                onValueChange = { value ->
                    volumeSliderValue = (value * 100).roundToInt() / 100f
                },
                onValueChangeFinished = {
                    viewModel.onAction(MusicSettingAction.OnVolumeChange(volumeSliderValue))
                },
                valueRange = 0f..1f,
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    )
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = stylingState?.textColor ?: MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = stylingState?.textColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                )
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 8.dp),
                state = listState,
                content = {
                    items(
                        items = state.musicList,
                        key = { it.id!! }
                    ) { listItem ->
                        MyMusicItemView(
                            music = listItem,
                            stylingState = stylingState,
                            onFavoriteClick = { musicItem ->
                                viewModel.onAction(MusicSettingAction.OnFavoriteClick(musicItem))
                            },
                            onItemClick = { musicItem ->
                                viewModel.onAction(MusicSettingAction.OnItemClick(musicItem))
                            },
                            onDelete = { musicItem ->
                                viewModel.onAction(MusicSettingAction.OnDelete(musicItem))
                            }
                        )
                    }
                },
                contentPadding = PaddingValues(
                    bottom = WindowInsets
                        .systemBars
                        .union(WindowInsets.displayCutout)
                        .only(WindowInsetsSides.Bottom)
                        .asPaddingValues()
                        .calculateBottomPadding(),
                )
            )
        }
    }
}