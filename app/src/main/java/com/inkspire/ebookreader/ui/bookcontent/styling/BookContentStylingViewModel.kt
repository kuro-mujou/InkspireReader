package com.inkspire.ebookreader.ui.bookcontent.styling

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.BookContentStylingDatastoreUseCase
import com.inkspire.ebookreader.util.ColorUtil.darken
import com.inkspire.ebookreader.util.ColorUtil.isDark
import com.inkspire.ebookreader.util.ColorUtil.lighten
import com.inkspire.ebookreader.util.ColorUtil.toHsv
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class BookContentStylingViewModel(
    private val datastoreUseCase: BookContentStylingDatastoreUseCase
): ViewModel() {
    private val _state = MutableStateFlow(StylingState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    init {
        viewModelScope.launch {
            datastoreUseCase.getTextColor().collectLatest { color->
                _state.update {
                    it.copy(
                        textColor = Color(color),
                        tocTextColor = generateTOCTextColor(_state.value.backgroundColor),
                        textBackgroundColor = generateTextSelectionColor(
                            _state.value.backgroundColor,
                            Color(color)
                        ),
                    )
                }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getBackgroundColor().collectLatest { color ->
                _state.update {
                    it.copy(
                        backgroundColor = Color(color),
                        tocTextColor = generateTOCTextColor(Color(color)),
                        textBackgroundColor = generateTextSelectionColor(Color(color), _state.value.textColor),
                        containerColor = generateContainerColor(Color(color)),
                    )
                }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getSelectedColorSet().collectLatest { colorSetIndex ->
                _state.update { it.copy(selectedColorSet = colorSetIndex) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getFontSize().collectLatest { fontSize ->
                _state.update { it.copy(fontSize = fontSize) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getFontFamily().collectLatest { fontFamily ->
                _state.update { it.copy(selectedFontFamilyIndex = fontFamily) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getLineSpacing().collectLatest { lineSpacing ->
                _state.update { it.copy(lineSpacing = lineSpacing) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getTextAlign().collectLatest { textAlign ->
                _state.update { it.copy(textAlign = textAlign) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getTextIndent().collectLatest { textIndent ->
                _state.update { it.copy(textIndent = textIndent) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getImagePaddingState().collectLatest { imagePaddingState ->
                _state.update { it.copy(imagePaddingState = imagePaddingState) }
            }
        }
    }

    fun onAction(action: BookContentStylingAction) {
        when (action) {

            else -> {}
        }
    }

    private fun generateContainerColor(backgroundColor: Color): Color {
        return if (backgroundColor.isDark()) {
            backgroundColor.lighten(0.05f)
        } else {
            backgroundColor.darken(0.05f)
        }
    }

    private fun generateTextSelectionColor(backgroundColor: Color, textColor: Color): Color {
        val (bgH, bgS, bgV) = backgroundColor.toHsv()
        val (txtH, txtS, txtV) = textColor.toHsv()
        val h = circularHueAverage(bgH, txtH)
        val s = (bgS + txtS) / 2
        val rawV = (bgV + txtV) / 2
        val v = when {
            bgV > 0.7f && txtV > 0.7f -> 0.3f
            bgV < 0.3f && txtV < 0.3f -> 0.7f
            else -> rawV
        }
        return Color.hsv(h, s, v).copy(alpha = 0.8f)
    }

    private fun circularHueAverage(h1: Float, h2: Float): Float {
        val diff = abs(h1 - h2)
        return if (diff > 180) {
            (h1 + h2 + 360) / 2 % 360
        } else {
            (h1 + h2) / 2
        }
    }

    private fun generateTOCTextColor(backgroundColor: Color): Color {
        val tocTextColor: Color = if (backgroundColor.isDark()) {
            Color.White
        } else {
            Color.Black
        }
        return tocTextColor
    }
}