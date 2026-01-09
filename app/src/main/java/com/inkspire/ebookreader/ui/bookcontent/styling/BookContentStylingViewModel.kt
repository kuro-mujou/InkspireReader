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
            datastoreUseCase.stylePreferences.collectLatest { prefs ->

                val bgColor = prefs.backgroundColor
                val txtColor = prefs.textColor

                _state.update { currentState ->
                    currentState.copy(
                        stylePreferences = prefs,
                        tocTextColor = generateTOCTextColor(bgColor),
                        textBackgroundColor = generateTextSelectionColor(bgColor, txtColor),
                        containerColor = generateContainerColor(bgColor),
                        drawerContainerColor = generateDrawerContainerColor(bgColor)
                    )
                }
            }
        }
    }

    fun onAction(action: BookContentStylingAction) {
        viewModelScope.launch {
            when (action) {
                is BookContentStylingAction.UpdateBackgroundColor -> datastoreUseCase.setBackgroundColor(action.color)
                is BookContentStylingAction.UpdateFontSize -> datastoreUseCase.setFontSize(action.fontSize)
                is BookContentStylingAction.UpdateImagePaddingState -> datastoreUseCase.setImagePaddingState(!_state.value.stylePreferences.imagePaddingState)
                is BookContentStylingAction.UpdateLineSpacing -> datastoreUseCase.setLineSpacing(action.lineSpacing)
                is BookContentStylingAction.UpdateSelectedColorSet -> datastoreUseCase.setSelectedColorSet(action.index)
                is BookContentStylingAction.UpdateSelectedFontFamilyIndex -> datastoreUseCase.setFontFamily(action.index)
                is BookContentStylingAction.UpdateTextHighlight -> datastoreUseCase.setTextHighlight(!_state.value.stylePreferences.enableHighlight)
                is BookContentStylingAction.UpdateTextColor -> datastoreUseCase.setTextColor(action.color)
                is BookContentStylingAction.UpdateTextIndent -> datastoreUseCase.setTextIndent(!_state.value.stylePreferences.textIndent)
            }
        }
    }

    private fun generateContainerColor(backgroundColor: Color): Color {
        return if (backgroundColor.isDark()) {
            backgroundColor.lighten(0.05f)
        } else {
            backgroundColor.darken(0.05f)
        }
    }

    private fun generateDrawerContainerColor(backgroundColor: Color): Color {
        return if (backgroundColor.isDark()) {
            backgroundColor.lighten(0.1f)
        } else {
            backgroundColor.darken(0.1f)
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