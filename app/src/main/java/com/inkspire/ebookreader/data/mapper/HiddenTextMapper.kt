package com.inkspire.ebookreader.data.mapper

import com.inkspire.ebookreader.data.database.model.HiddenTextEntity
import com.inkspire.ebookreader.domain.model.HiddenText

fun HiddenTextEntity.toModel() : HiddenText {
    return HiddenText (
        id = id,
        textToHide = textToHide,
    )
}