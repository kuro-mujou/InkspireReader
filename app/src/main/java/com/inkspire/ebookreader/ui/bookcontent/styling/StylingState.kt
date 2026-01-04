package com.inkspire.ebookreader.ui.bookcontent.styling

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.domain.model.ContentThemeColor
import com.inkspire.ebookreader.domain.model.StylePreferences

@Immutable
data class StylingState(
    val stylePreferences: StylePreferences = StylePreferences(),

    val tocTextColor: Color = Color(0xFFFFFFFF),
    val textBackgroundColor: Color = Color(0xFFFFFFFF),
    val containerColor: Color = Color(0xFFFFFFFF),
    val drawerContainerColor: Color = Color(0xFFFFFFFF),

    val fontFamilies: List<FontFamily> = listOf(
        FontFamily(Font(R.font.cormorant)),//serif
        FontFamily(Font(R.font.ibm_plex_serif)),//serif
        FontFamily(Font(R.font.literata)),//serif
        FontFamily(Font(R.font.noto_serif)),//serif
        FontFamily(Font(R.font.playfair_display)),//serif
        FontFamily(Font(R.font.source_serif_4)),//serif
        FontFamily(Font(R.font.source_serif_pro)),//serif
        FontFamily(Font(R.font.noto_sans)),//san
        FontFamily(Font(R.font.open_sans)),//san
        FontFamily(Font(R.font.roboto)),//san
        FontFamily(Font(R.font.source_sans_pro)),//san
    ),
    val fontNames: List<String> = listOf(
        "Cormorant",
        "IBM Plex Serif",
        "Literata",
        "Noto Serif",
        "Playfair Display",
        "Source Serif 4",
        "Source Serif Pro",
        "Noto Sans",
        "Open Sans",
        "Roboto",
        "Source Sans Pro",
    ),
    val contentThemeColors: List<ContentThemeColor> = listOf(
        ContentThemeColor(
            colorBg = Color(0xFFF1F7ED),
            colorTxt = Color(0xFF1B310E)
        ),
        ContentThemeColor(
            colorBg = Color(0xFFD3C3A3),
            colorTxt = Color(0xFF3A3129)
        ),
        ContentThemeColor(
            colorBg = Color(0xFFA2C0E5),
            colorTxt = Color(0xFF1B310E)
        ),
        ContentThemeColor(
            colorBg = Color(0xFFF3C9D7),
            colorTxt = Color(0xFF1B310E)
        ),
        ContentThemeColor(
            colorBg = Color(0xFFF7F4DC),
            colorTxt = Color(0xFF46422E)
        ),
        ContentThemeColor(
            colorBg = Color(0xFFFFFFFF),
            colorTxt = Color(0xFF3A342B)
        ),
        ContentThemeColor(
            colorBg = Color(0xFFC2E0CD),
            colorTxt = Color(0xFF334B39)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF393030),
            colorTxt = Color(0xFF95938F)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF051C2C),
            colorTxt = Color(0xFF637079)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF333333),
            colorTxt = Color(0xFFCCE8CF)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF152B06),
            colorTxt = Color(0xFF607057)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF151C1F),
            colorTxt = Color(0xFF4D5052)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF000000),
            colorTxt = Color(0xFF5F5F5F)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF000000),
            colorTxt = Color(0xFF494949)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF001622),
            colorTxt = Color(0xFF204353)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF171F27),
            colorTxt = Color(0xFF445053)
        ),
        ContentThemeColor(
            colorBg = Color(0xFF251C05),
            colorTxt = Color(0xFF574F3C)
        ),
    )
)