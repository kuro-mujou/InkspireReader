package com.inkspire.ebookreader.data.mapper

import com.inkspire.ebookreader.data.database.model.TableOfContentEntity
import com.inkspire.ebookreader.domain.model.TableOfContent

fun TableOfContentEntity.toDataClass(): TableOfContent {
    return TableOfContent(
        tocId = tocId,
        bookId = bookId,
        title = title,
        index = index,
        isFavorite = isFavorite
    )
}

fun TableOfContent.toEntity(): TableOfContentEntity {
    return TableOfContentEntity(
        bookId = bookId,
        title = title,
        index = index
    )
}