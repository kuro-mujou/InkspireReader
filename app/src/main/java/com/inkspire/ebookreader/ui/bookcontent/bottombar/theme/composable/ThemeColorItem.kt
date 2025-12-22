package com.inkspire.ebookreader.ui.bookcontent.bottombar.theme.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.domain.model.ContentThemeColor
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun ThemeColorItem(
    index: Int,
    selected: Boolean,
    colorSample: ContentThemeColor,
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
            .size(40.dp)
            .clip(CircleShape)
            .background(color = colorSample.colorBg)
            .border(
                width = 2.dp,
                color = if (selected) colorSample.colorTxt else colorSample.colorBg,
                shape = CircleShape
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Aa",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = colorSample.colorTxt,
                fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex]
            )
        )
    }
}