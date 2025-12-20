package com.inkspire.ebookreader.ui.setting.music

import android.content.Context
import android.net.Uri
import com.inkspire.ebookreader.domain.model.MusicItem

sealed interface MusicSettingAction {
    data class OnAddPerform(val uri: Uri, val context: Context) : MusicSettingAction
    data class OnFavoriteClick(val musicItem: MusicItem) : MusicSettingAction
    data class OnItemClick(val musicItem: MusicItem) : MusicSettingAction
    data class OnDelete(val musicItem: MusicItem) : MusicSettingAction
    data class OnVolumeChange(val volume: Float) : MusicSettingAction
    data class OnEnableBackgroundMusicChange(val enable: Boolean) : MusicSettingAction
}