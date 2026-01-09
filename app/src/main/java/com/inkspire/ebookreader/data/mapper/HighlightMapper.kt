package com.inkspire.ebookreader.data.mapper

import com.inkspire.ebookreader.data.database.model.HighlightEntity
import com.inkspire.ebookreader.domain.model.Highlight
import com.inkspire.ebookreader.domain.model.HighlightToInsert

fun HighlightEntity.toDataClass() : Highlight {
    return Highlight(
        paragraphIndex = paragraphIndex,
        startOffset = startOffset,
        endOffset = endOffset,
        colorIndex = colorIndex,
        createdTime = createdTime,
        note = note
    )
}

fun HighlightToInsert.toEntity() : HighlightEntity {
    return HighlightEntity(
        bookId = bookId,
        tocId = tocId,
        paragraphIndex = paragraphIndex,
        startOffset = startOffset,
        endOffset = endOffset,
        colorIndex = colorIndex,
        note = note
    )
}