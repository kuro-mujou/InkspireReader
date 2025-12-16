package com.inkspire.ebookreader.ui.music

import android.content.Context
import android.net.Uri
import com.inkspire.ebookreader.domain.model.MusicItem

sealed interface MusicListAction {
    data class OnAddPerform(val uri: Uri, val context: Context) : MusicListAction
    data class OnFavoriteClick(val musicItem: MusicItem) : MusicListAction
    data class OnItemClick(val musicItem: MusicItem) : MusicListAction
    data class OnDelete(val musicItem: MusicItem) : MusicListAction
    data class OnVolumeChange(val volume: Float) : MusicListAction
}