package com.inkspire.ebookreader.domain.model

data class MusicState(
    val musicList: List<MusicItem> = emptyList(),
    val playerVolume: Float = 1f,
)
