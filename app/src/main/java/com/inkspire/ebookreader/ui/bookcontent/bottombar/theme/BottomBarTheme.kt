package com.inkspire.ebookreader.ui.bookcontent.bottombar.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.ui.bookcontent.bottombar.theme.composable.ThemeColorItem
import com.inkspire.ebookreader.ui.bookcontent.bottombar.theme.composable.ThemeFontItem
import com.inkspire.ebookreader.ui.bookcontent.colorpicker.ColorPicker
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.styling.BookContentStylingAction
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlin.math.roundToInt

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomBarTheme(
    hazeState: HazeState,
    hazeEnableProvider: () -> Boolean,
    bookInfoProvider: () -> Book,
) {
    val stylingVM = LocalStylingViewModel.current
    val stylingState by stylingVM.state.collectAsStateWithLifecycle()

    val style = HazeMaterials.thin(stylingState.containerColor)

    var openColorPickerForBackground by remember { mutableStateOf(false) }
    var openColorPickerForText by remember { mutableStateOf(false) }
    var openChangeColorMenu by remember { mutableStateOf(false) }

    var textWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    if (openColorPickerForBackground) {
        ColorPicker(
            onDismiss = {
                openColorPickerForBackground = false
            },
            onColorSelected = {
                stylingVM.onAction(BookContentStylingAction.UpdateBackgroundColor(it.toArgb()))
                stylingVM.onAction(BookContentStylingAction.UpdateSelectedColorSet(18))
                openColorPickerForBackground = false
            }
        )
    }
    if (openColorPickerForText) {
        ColorPicker(
            onDismiss = {
                openColorPickerForText = false
            },
            onColorSelected = {
                stylingVM.onAction(BookContentStylingAction.UpdateTextColor(it.toArgb()))
                stylingVM.onAction(BookContentStylingAction.UpdateSelectedColorSet(18))
                openColorPickerForText = false
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(
                if (hazeEnableProvider()) {
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
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = openChangeColorMenu
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .weight(1f)
                            .height(40.dp)
                            .border(width = 2.dp, color = Color.Gray)
                            .background(color = stylingState.containerColor)
                            .clickable {
                                openColorPickerForBackground = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Background",
                            style = TextStyle(
                                color = stylingState.stylePreferences.textColor,
                                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f)
                            .height(40.dp)
                            .border(width = 2.dp, color = Color.Gray)
                            .background(color = stylingState.stylePreferences.textColor)
                            .clickable {
                                openColorPickerForText = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Text",
                            style = TextStyle(
                                color = stylingState.stylePreferences.backgroundColor,
                                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                            )
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 2.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                        .wrapContentHeight()
                ) {
                    itemsIndexed(
                        items = stylingState.contentThemeColors,
                    ) { index, sample ->
                        ThemeColorItem(
                            index = index,
                            colorSample = sample,
                            stylingState = stylingState,
                            selected = stylingState.stylePreferences.selectedColorSet == index,
                            onClick = {
                                stylingVM.onAction(BookContentStylingAction.UpdateSelectedColorSet(index))
                                stylingVM.onAction(BookContentStylingAction.UpdateBackgroundColor(sample.colorBg.toArgb()))
                                stylingVM.onAction(BookContentStylingAction.UpdateTextColor(sample.colorTxt.toArgb()))
                            }
                        )
                    }
                }
                OutlinedIconButton(
                    onClick = {
                        openChangeColorMenu = !openChangeColorMenu
                    },
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = stylingState.stylePreferences.backgroundColor
                    ),
                    border = BorderStroke(width = 2.dp, color = stylingState.stylePreferences.textColor)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                        tint = stylingState.stylePreferences.textColor,
                        contentDescription = null
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 2.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                        .wrapContentHeight(),
                ) {
                    itemsIndexed(
                        items = stylingState.fontFamilies,
                    ) { index, sample ->
                        ThemeFontItem(
                            index = index,
                            fontSample = sample,
                            fontName = stylingState.fontNames[index],
                            selected = stylingState.stylePreferences.fontFamily == index,
                            stylingState = stylingState,
                            onClick = {
                                stylingVM.onAction(BookContentStylingAction.UpdateSelectedFontFamilyIndex(index))
                            }
                        )
                    }
                }
                OutlinedIconButton(
                    onClick = {
                        stylingVM.onAction(BookContentStylingAction.UpdateImagePaddingState)
                    },
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = if (stylingState.stylePreferences.imagePaddingState) stylingState.stylePreferences.textColor else Color.Transparent
                    ),
                    border = BorderStroke(width = 2.dp, color = stylingState.stylePreferences.textColor)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_image_padding),
                        contentDescription = null,
                        tint = if (stylingState.stylePreferences.imagePaddingState) stylingState.stylePreferences.backgroundColor else stylingState.stylePreferences.textColor
                    )
                }
            }
            if (bookInfoProvider().fileType != "cbz" && bookInfoProvider().fileType != "pdf/images") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.width(with(density) { textWidth.toDp() }),
                        text = "Font Size",
                        style = TextStyle(
                            color = stylingState.stylePreferences.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                        )
                    )
                    Slider(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        value = stylingState.stylePreferences.fontSize.toFloat(),
                        onValueChange = { value ->
                            stylingVM.onAction(BookContentStylingAction.UpdateFontSize(value.roundToInt()))
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor = stylingState.stylePreferences.textColor,
                            activeTickColor = stylingState.containerColor,
                            inactiveTickColor = stylingState.containerColor.copy(alpha = 0.5f),
                            inactiveTrackColor = stylingState.stylePreferences.textColor.copy(alpha = 0.5f),
                        ),
                        valueRange = 12f..48f,
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = stylingState.stylePreferences.textColor,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${stylingState.stylePreferences.fontSize}",
                                    style = TextStyle(
                                        color = stylingState.containerColor,
                                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                                    )
                                )
                            }
                        },
                        steps = 8
                    )
                    OutlinedIconButton(
                        onClick = {
                            stylingVM.onAction(BookContentStylingAction.UpdateTextAlign)
                        },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = stylingState.stylePreferences.textColor
                        ),
                        border = BorderStroke(width = 2.dp, color = stylingState.stylePreferences.textColor)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = if (stylingState.stylePreferences.textAlign)
                                    R.drawable.ic_align_justify
                                else
                                    R.drawable.ic_align_left
                            ),
                            contentDescription = null,
                            tint = stylingState.stylePreferences.backgroundColor
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.onGloballyPositioned {
                            textWidth = it.size.width
                        },
                        text = "Line Spacing",
                        style = TextStyle(
                            color = stylingState.stylePreferences.textColor,
                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                        )
                    )
                    Slider(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        value = stylingState.stylePreferences.lineSpacing.toFloat(),
                        onValueChange = { value ->
                            stylingVM.onAction(BookContentStylingAction.UpdateLineSpacing(value.roundToInt()))
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor = stylingState.stylePreferences.textColor,
                            activeTickColor = stylingState.containerColor,
                            inactiveTickColor = stylingState.containerColor.copy(alpha = 0.5f),
                            inactiveTrackColor = stylingState.stylePreferences.textColor.copy(alpha = 0.5f),
                        ),
                        valueRange = 4f..24f,
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = stylingState.stylePreferences.textColor,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${stylingState.stylePreferences.lineSpacing}",
                                    style = TextStyle(
                                        color = stylingState.containerColor,
                                        fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily]
                                    )
                                )
                            }
                        },
                        steps = 9
                    )
                    OutlinedIconButton(
                        onClick = {
                            stylingVM.onAction(BookContentStylingAction.UpdateTextIndent)
                        },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            if (stylingState.stylePreferences.textIndent) stylingState.stylePreferences.textColor else Color.Transparent
                        ),
                        border = BorderStroke(width = 2.dp, color = stylingState.stylePreferences.textColor)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_text_indent),
                            contentDescription = null,
                            tint = if (stylingState.stylePreferences.textIndent) stylingState.stylePreferences.backgroundColor else stylingState.stylePreferences.textColor
                        )
                    }
                }
            }
        }
    }
}