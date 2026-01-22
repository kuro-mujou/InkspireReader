package com.inkspire.ebookreader.ui.setting.hiddentext

import com.inkspire.ebookreader.domain.model.HiddenText

data class HiddenTextState(
    val hiddenTexts: List<HiddenText> = emptyList(),
)
