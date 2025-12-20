package com.inkspire.ebookreader.ui.setting.music

import com.inkspire.ebookreader.domain.model.MusicItem

data class MusicSettingState (
    val enableBackgroundMusic : Boolean = false,
    val musicList: List<MusicItem> = emptyList(),
    val playerVolume: Float = 1f,
)