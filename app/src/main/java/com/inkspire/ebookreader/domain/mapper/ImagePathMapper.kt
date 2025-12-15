package com.inkspire.ebookreader.domain.mapper

import com.inkspire.ebookreader.data.model.ImagePathEntity
import com.inkspire.ebookreader.domain.model.ImagePath

fun ImagePath.toEntity(): ImagePathEntity {
    return ImagePathEntity(
        bookId = bookId,
        imagePath = imagePath
    )
}

fun ImagePathEntity.toDataClass(): ImagePath {
    return ImagePath(
        bookId = bookId,
        imagePath = imagePath
    )
}