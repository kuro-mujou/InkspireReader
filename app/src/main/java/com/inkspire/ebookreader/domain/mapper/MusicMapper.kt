package com.inkspire.ebookreader.domain.mapper

import com.inkspire.ebookreader.data.model.MusicPathEntity
import com.inkspire.ebookreader.domain.model.MusicItem

fun MusicItem.toEntity(): MusicPathEntity {
    return MusicPathEntity(
        name = name!!,
        uri = uri!!,
    )
}

fun MusicPathEntity.toDataClass(): MusicItem {
    return MusicItem(
        id = id,
        name = name,
        uri = uri,
        isFavorite = isFavorite,
        isSelected = isSelected
    )
}