package com.inkspire.ebookreader.data.mapper

import com.inkspire.ebookreader.data.database.model.ImagePathEntity
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