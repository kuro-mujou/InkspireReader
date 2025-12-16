package com.inkspire.ebookreader.ui.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MyColorRails(
    selectedColorSet: Int,
    onClick: (Int, Color) -> Unit
) {
    val lightModeRainbow = listOf(
        Color(0xFFE57373), // Red
        Color(0xFFF06292), // Pink
        Color(0xFFBA68C8), // Purple
        Color(0xFF9575CD), // Deep Purple
        Color(0xFF7986CB), // Indigo
        Color(0xFF64B5F6), // Blue
        Color(0xFF4FC3F7), // Light Blue
        Color(0xFF4DD0E1), // Cyan
        Color(0xFF4DB6AC), // Teal
        Color(0xFF81C784), // Green
        Color(0xFFAED581), // Light Green
        Color(0xFFFFD54F), // Amber
        Color(0xFFFFB300), // Orange
        Color(0xFFFF8A65)  // Deep Orange
    )

    val darkModeRainbow = listOf(
        Color(0xFFEF9A9A), // Light Red
        Color(0xFFF48FB1), // Light Pink
        Color(0xFFCE93D8), // Light Purple
        Color(0xFFB39DDB), // Light Deep Purple
        Color(0xFF9FA8DA), // Light Indigo
        Color(0xFF90CAF9), // Light Blue
        Color(0xFF81D4FA), // Lighter Blue
        Color(0xFF80DEEA), // Light Cyan
        Color(0xFF80CBC4), // Light Teal
        Color(0xFFA5D6A7), // Light Green
        Color(0xFFC5E1A5), // Lighter Green
        Color(0xFFFFE082), // Light Amber
        Color(0xFFFFCA28), // Light Orange
        Color(0xFFFFCC80)  // Light Deep Orange
    )
    val isDarkTheme = isSystemInDarkTheme()
    val rainbowColors = remember(isDarkTheme) {
        if (isDarkTheme) darkModeRainbow else lightModeRainbow
    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        itemsIndexed(
            items = rainbowColors,
            key = { index: Int, _: Color -> index }
        ) { index, color ->
            MyColorRailItem(
                selected = index == selectedColorSet,
                color = color,
                onClick = { onClick(index, color) },
            )
        }
    }
}