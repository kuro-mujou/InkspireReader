package com.inkspire.ebookreader.ui.setting.autoscroll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.inkspire.ebookreader.ui.bookcontent.autoscroll.AutoScrollState
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoScrollSetting(
    autoScrollState: AutoScrollState,
    stylingState: StylingState?,
    onDismissRequest: () -> Unit
) {
    val viewModel = koinViewModel<AutoScrollSettingViewModel>()
    val minSpeed = 0.1f
    val maxSpeed = 1.0f
    val sumRange = minSpeed + maxSpeed

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        var speedSliderValue by remember(autoScrollState.autoScrollSpeed) {
            mutableFloatStateOf(sumRange - (autoScrollState.autoScrollSpeed / 20000f))
        }
        var delayAtStart by remember { mutableIntStateOf(autoScrollState.delayTimeAtStart) }
        var delayAtEnd by remember { mutableIntStateOf(autoScrollState.delayTimeAtEnd) }
        var delayResumeMode by remember { mutableIntStateOf(autoScrollState.autoScrollResumeDelayTime) }
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = stylingState?.backgroundColor ?: MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 4.dp),
                    text = "Auto Scroll Setting",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Slower",
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Speed",
                        fontWeight = FontWeight.Medium,
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Faster",
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                }
                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = speedSliderValue,
                    onValueChange = { value ->
                        speedSliderValue = value
                    },
                    onValueChangeFinished = {
                        val logicValue = sumRange - speedSliderValue
                        val finalSpeed = (logicValue * 20000).roundToInt()
                        viewModel.onAction(AutoScrollSettingAction.UpdateScrollSpeed(finalSpeed))
                    },
                    valueRange = minSpeed..maxSpeed,
                    steps = 17,
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
                        activeTickColor = stylingState?.backgroundColor ?: MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = stylingState?.textColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                        inactiveTickColor = stylingState?.backgroundColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Delay at start",
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "%.2fs".format(delayAtStart / 1000f),
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                }
                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = delayAtStart / 1000f,
                    onValueChange = { value ->
                        delayAtStart = (value * 1000).roundToInt()
                    },
                    onValueChangeFinished = {
                        viewModel.onAction(AutoScrollSettingAction.UpdateDelayAtStart(delayAtStart))
                    },
                    valueRange = 1f..10f,
                    steps = 8,
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
                        activeTickColor = stylingState?.backgroundColor ?: MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = stylingState?.textColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                        inactiveTickColor = stylingState?.backgroundColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Delay at end",
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "%.2fs".format(delayAtEnd / 1000f),
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                }
                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = delayAtEnd / 1000f,
                    onValueChange = { value ->
                        delayAtEnd = (value * 1000).roundToInt()
                    },
                    onValueChangeFinished = {
                        viewModel.onAction(AutoScrollSettingAction.UpdateDelayAtEnd(delayAtEnd))
                    },
                    valueRange = 1f..10f,
                    steps = 8,
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
                        activeTickColor = stylingState?.backgroundColor ?: MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = stylingState?.textColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                        inactiveTickColor = stylingState?.backgroundColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                    )
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = stylingState?.textColor ?: MaterialTheme.colorScheme.outlineVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Auto Scroll after pause",
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                        color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = autoScrollState.autoScrollResumeMode,
                        onCheckedChange = {
                            viewModel.onAction(AutoScrollSettingAction.UpdateAutoResumeScrollMode(it))
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
                if (autoScrollState.autoScrollResumeMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Delay time",
                            fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                            color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "%.2fs".format(delayResumeMode / 1000f),
                            fontFamily = stylingState?.fontFamilies?.get(stylingState.selectedFontFamilyIndex),
                            color = stylingState?.textColor ?: MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = delayResumeMode / 1000f,
                        onValueChange = { value ->
                            delayResumeMode = (value * 1000).roundToInt()
                        },
                        onValueChangeFinished = {
                            viewModel.onAction(AutoScrollSettingAction.UpdateDelayResumeMode(delayResumeMode))
                        },
                        valueRange = 1f..5f,
                        steps = 3,
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
                            activeTickColor = stylingState?.backgroundColor ?: MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = stylingState?.textColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                            inactiveTickColor = stylingState?.backgroundColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.secondaryContainer,
                        )
                    )
                }
            }
        }
    }
}