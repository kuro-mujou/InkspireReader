package com.inkspire.ebookreader.ui.setting.music

import androidx.compose.runtime.Immutable
import com.inkspire.ebookreader.domain.model.MusicItem
import com.inkspire.ebookreader.domain.model.MusicPreferences

@Immutable
data class MusicSettingState (
    val musicPreferences: MusicPreferences = MusicPreferences(),
    val musicList: List<MusicItem> = emptyList(),
)