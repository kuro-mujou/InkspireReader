package com.inkspire.ebookreader.ui.setting.hiddentext

import com.inkspire.ebookreader.domain.model.HiddenText

sealed interface HiddenTextAction {
    data object OnDeleteHiddenTexts: HiddenTextAction
    data class ToggleHiddenTextSelectedState(val item: HiddenText): HiddenTextAction
}