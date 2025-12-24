package com.inkspire.ebookreader.ui.setting.tts

import android.speech.tts.TextToSpeech
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.ui.bookcontent.tts.TTSManager
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.setting.tts.common.TTSSettingScreenType
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TTSSetting(
    stylingState: StylingState? = null,
    onDismiss: () -> Unit,
) {
    val viewModel = koinViewModel<TTSSettingViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val ttsManager = koinInject<TTSManager>()
    val tts = ttsManager.getTTS()

    var speedSliderValue by remember { mutableFloatStateOf(state.currentSpeed) }
    var pitchSliderValue by remember { mutableFloatStateOf(state.currentPitch) }

    val configuration = LocalWindowInfo.current.containerSize
    val screenHeight = configuration.height.dp

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val dialogWidth = when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT -> 0.8f
        DeviceConfiguration.PHONE_LANDSCAPE -> 0.6f
        DeviceConfiguration.TABLET_PORTRAIT -> 0.7f
        DeviceConfiguration.TABLET_LANDSCAPE -> 0.5f
    }
    val dialogHeight = when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT -> 0.6f
        DeviceConfiguration.PHONE_LANDSCAPE -> 0.9f
        DeviceConfiguration.TABLET_PORTRAIT -> 0.6f
        DeviceConfiguration.TABLET_LANDSCAPE -> 0.9f
    }

    Dialog(
        onDismissRequest = {
            if (state.currentVoice == null) {
                tts?.let { viewModel.onAction(TTSSettingAction.FixNullVoice(it)) }
            }
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val focusManager = LocalFocusManager.current
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth(dialogWidth)
                .heightIn(max = screenHeight * dialogHeight)
                .wrapContentHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        focusManager.clearFocus()
                    }
                ),
            color = stylingState?.backgroundColor ?: MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = when(state.selectedScreenType) {
                        TTSSettingScreenType.NORMAL_SETTING -> "Normal Setting"
                        TTSSettingScreenType.LANGUAGE_SETTING -> "Language Setting"
                        TTSSettingScreenType.VOICE_SETTING -> "Voice Setting"
                    },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                )
                HorizontalDivider(
                    thickness = 2.dp,
                    color = stylingState?.textColor ?: MaterialTheme.colorScheme.outlineVariant
                )
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                ) {
                    Crossfade(state.selectedScreenType) { targetState ->
                        when (targetState) {
                            TTSSettingScreenType.NORMAL_SETTING -> {
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, bottom = 4.dp)
                                            .border(
                                                width = 1.dp,
                                                color = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clip(shape = RoundedCornerShape(8.dp)),
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .background(
                                                    color = stylingState?.containerColor
                                                        ?: MaterialTheme.colorScheme.surfaceVariant
                                                )
                                                .padding(8.dp)
                                                .fillMaxWidth()
                                                .clickable(
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() },
                                                ) {
                                                    viewModel.onAction(
                                                        TTSSettingAction.UpdateScreenType(
                                                            TTSSettingScreenType.LANGUAGE_SETTING
                                                        )
                                                    )
                                                },
                                        ) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_setting),
                                                contentDescription = null,
                                                tint = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Language",
                                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                                                contentDescription = null,
                                                tint = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = state.currentLanguage?.displayName ?: "null",
                                            modifier = Modifier.padding(8.dp),
                                            color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, bottom = 4.dp)
                                            .border(
                                                width = 1.dp,
                                                color = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clip(shape = RoundedCornerShape(8.dp)),
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .background(
                                                    color = stylingState?.containerColor
                                                        ?: MaterialTheme.colorScheme.surfaceVariant
                                                )
                                                .padding(8.dp)
                                                .fillMaxWidth()
                                                .clickable(
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() },
                                                ) {
                                                    viewModel.onAction(
                                                        TTSSettingAction.UpdateScreenType(
                                                            TTSSettingScreenType.VOICE_SETTING
                                                        )
                                                    )
                                                },
                                        ) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_setting),
                                                contentDescription = null,
                                                tint = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Voice",
                                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                                                contentDescription = null,
                                                tint = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Row(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = state.currentVoice?.name ?: "null",
                                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                            )
                                            state.currentVoice?.let { voice ->
                                                Text(
                                                    text = "-",
                                                    color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                                )
                                                Text(
                                                    text = voice.quality.toString(),
                                                    color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                                )
                                            }
                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, bottom = 4.dp)
                                            .border(
                                                width = 1.dp,
                                                color = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .padding(horizontal = 8.dp)
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Speed",
                                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                            )
                                            Text(
                                                text = "%.2fx".format(speedSliderValue),
                                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                            )
                                        }
                                        Slider(
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp)
                                                .fillMaxWidth(),
                                            value = speedSliderValue,
                                            onValueChange = { value ->
                                                speedSliderValue = (value * 100).roundToInt() / 100f
                                            },
                                            onValueChangeFinished = {
                                                viewModel.onAction(
                                                    TTSSettingAction.UpdateSpeed(
                                                        speedSliderValue
                                                    )
                                                )
                                            },
                                            valueRange = 0.5f..2.5f,
                                            thumb = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(
                                                            color = stylingState?.textColor
                                                                ?: MaterialTheme.colorScheme.primary,
                                                            shape = CircleShape
                                                        )
                                                )
                                            },
                                            colors = SliderDefaults.colors(
                                                activeTrackColor = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.primary,
                                                inactiveTrackColor = stylingState?.textColor?.copy(
                                                    alpha = 0.5f
                                                ) ?: MaterialTheme.colorScheme.secondaryContainer,
                                            )
                                        )
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp)
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Pitch",
                                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                            )
                                            Text(
                                                text = "%.2fx".format(pitchSliderValue),
                                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                            )
                                        }
                                        Slider(
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp)
                                                .fillMaxWidth(),
                                            value = pitchSliderValue,
                                            onValueChange = { value ->
                                                pitchSliderValue = (value * 100).roundToInt() / 100f
                                            },
                                            onValueChangeFinished = {
                                                viewModel.onAction(
                                                    TTSSettingAction.UpdatePitch(
                                                        pitchSliderValue
                                                    )
                                                )
                                            },
                                            valueRange = 0.5f..1.5f,
                                            thumb = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(
                                                            color = stylingState?.textColor
                                                                ?: MaterialTheme.colorScheme.primary,
                                                            shape = CircleShape
                                                        )
                                                )
                                            },
                                            colors = SliderDefaults.colors(
                                                activeTrackColor = stylingState?.textColor
                                                    ?: MaterialTheme.colorScheme.primary,
                                                inactiveTrackColor = stylingState?.textColor?.copy(
                                                    alpha = 0.5f
                                                ) ?: MaterialTheme.colorScheme.secondaryContainer,
                                            )
                                        )
                                    }
                                }
                            }

                            TTSSettingScreenType.LANGUAGE_SETTING -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                ) {
                                    items(state.languages) { language ->
                                        Row(
                                            modifier = Modifier
                                                .then(
                                                    if (language == state.currentLanguage) {
                                                        Modifier.background(
                                                            color = stylingState?.textBackgroundColor
                                                                ?: MaterialTheme.colorScheme.primaryContainer,
                                                            shape = RoundedCornerShape(16.dp)
                                                        )
                                                    } else {
                                                        Modifier
                                                    }
                                                )
                                                .fillMaxWidth()
                                                .padding(4.dp)
                                                .clickable(
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    onClick = {
                                                        viewModel.onAction(
                                                            TTSSettingAction.UpdateLanguage(
                                                                language
                                                            )
                                                        )
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = language.displayName,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .weight(1f),
                                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                            )
                                            if (language == state.currentLanguage) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_confirm),
                                                    contentDescription = null,
                                                    tint = stylingState?.textColor
                                                        ?: MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            TTSSettingScreenType.VOICE_SETTING -> {
                                val filteredVoices = state.voices.filter {
                                    it.locale == state.currentLanguage
                                }
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .fillMaxWidth(),
                                ) {
                                    items(filteredVoices) { voice ->
                                        Row(
                                            modifier = Modifier
                                                .then(
                                                    if (voice == state.currentVoice) {
                                                        Modifier.background(
                                                            color = stylingState?.textBackgroundColor
                                                                ?: MaterialTheme.colorScheme.primaryContainer,
                                                            shape = RoundedCornerShape(16.dp)
                                                        )
                                                    } else {
                                                        Modifier
                                                    }
                                                )
                                                .fillMaxWidth()
                                                .padding(4.dp)
                                                .clickable(
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    onClick = {
                                                        viewModel.onAction(
                                                            TTSSettingAction.UpdateVoice(
                                                                voice
                                                            )
                                                        )
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(horizontal = 4.dp)
                                                    .weight(1f)
                                            ) {
                                                Text(
                                                    text = "Quality: " + voice.quality.toString(),
                                                    color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                                )
                                                Text(
                                                    text = voice.name,
                                                    color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                                                )
                                            }
                                            if (voice == state.currentVoice) {
                                                Icon(
                                                    modifier = Modifier.padding(end = 4.dp),
                                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_confirm),
                                                    contentDescription = null,
                                                    tint = stylingState?.textColor
                                                        ?: MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.selectedScreenType != TTSSettingScreenType.NORMAL_SETTING) {
                        Button(
                            onClick = {
                                viewModel.onAction(TTSSettingAction.UpdateScreenType(TTSSettingScreenType.NORMAL_SETTING))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = stylingState?.containerColor ?: MaterialTheme.colorScheme.primary,
                            )
                        ) {
                            Text(
                                text = "Done",
                                color = stylingState?.textColor ?: MaterialTheme.colorScheme.onPrimary,
                                fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            tts?.speak("xin chào", TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
                        },
                        border = BorderStroke(
                            width = 1.dp,
                            color = stylingState?.textColor ?: MaterialTheme.colorScheme.outlineVariant
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = "Test Voice",
                            color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        )
                    }
                }
            }
        }
    }
}
