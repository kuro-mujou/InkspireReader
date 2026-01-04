package com.inkspire.ebookreader.ui.bookcontent.bottombar.theme.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun ThemeFontItem(
    index: Int,
    fontSample: FontFamily,
    fontName: String,
    selected: Boolean,
    stylingState: StylingState,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(
                if (index == 0)
                    Modifier.padding(end = 8.dp)
                else
                    Modifier.padding(start = 8.dp, end = 8.dp)
            )
            .height(40.dp)
            .wrapContentWidth()
            .clip(CircleShape)
            .background(color = stylingState.stylePreferences.backgroundColor)
            .border(
                width = 2.dp,
                color = if (selected) stylingState.stylePreferences.textColor else stylingState.stylePreferences.backgroundColor,
                shape = CircleShape
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = fontName,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            style = TextStyle(
                fontFamily = fontSample,
                fontWeight = FontWeight.Bold,
                color = stylingState.stylePreferences.textColor,
            )
        )
    }
}