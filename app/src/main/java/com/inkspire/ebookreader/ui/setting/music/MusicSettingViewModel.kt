package com.inkspire.ebookreader.ui.setting.music


import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.model.MusicItem
import com.inkspire.ebookreader.domain.usecase.MusicSettingDatastoreUseCase
import com.inkspire.ebookreader.domain.usecase.MusicSettingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MusicSettingViewModel(
    private val musicSettingUseCase: MusicSettingUseCase,
    private val datastoreUseCase: MusicSettingDatastoreUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MusicSettingState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    init {
        viewModelScope.launch {
            musicSettingUseCase.getMusicPaths()
                .collectLatest { sortedMusicItems ->
                    _state.update {
                        it.copy(
                            musicList = sortedMusicItems
                        )
                    }
                }
        }
        viewModelScope.launch {
            datastoreUseCase.getEnableBackgroundMusic().collectLatest { enableBackgroundMusic ->
                _state.update { it.copy(enableBackgroundMusic = enableBackgroundMusic) }
            }
        }
        viewModelScope.launch {
            datastoreUseCase.getPlayerVolume().collectLatest { playerVolume ->
                _state.update { it.copy(playerVolume = playerVolume) }
            }
        }
    }

    fun onAction(action: MusicSettingAction) {
        when (action) {
            is MusicSettingAction.OnAddPerform -> {
                val fileName = getFileName(action.context, action.uri)
                val filePath = saveMusicToPrivateStorage(action.context, action.uri, fileName)
                viewModelScope.launch {
                    musicSettingUseCase.saveMusicPaths(
                        listOf(
                            MusicItem(
                                name = fileName,
                                uri = filePath
                            )
                        )
                    )
                }
            }

            is MusicSettingAction.OnFavoriteClick -> {
                viewModelScope.launch {
                    musicSettingUseCase.setMusicAsFavorite(
                        action.musicItem.id!!,
                        !action.musicItem.isFavorite
                    )
                }
            }

            is MusicSettingAction.OnItemClick -> {
                viewModelScope.launch {
                    musicSettingUseCase.setMusicAsSelected(
                        action.musicItem.id!!,
                        !action.musicItem.isSelected
                    )
                }
            }

            is MusicSettingAction.OnDelete -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            musicList = _state.value.musicList - action.musicItem
                        )
                    }
                    processDeleteMusic(action.musicItem)
                }
            }

            is MusicSettingAction.OnVolumeChange -> {
                viewModelScope.launch {
                    datastoreUseCase.setPlayerVolume(action.volume)
                }
            }

            is MusicSettingAction.OnEnableBackgroundMusicChange -> {
                viewModelScope.launch {
                    datastoreUseCase.setEnableBackgroundMusic(action.enable)
                }
            }
        }
    }

    private fun saveMusicToPrivateStorage(context: Context, uri: Uri, fileName: String): String {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, fileName)
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            "error"
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var fileName = "unknown"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    val name = cursor.getString(nameIndex)
                    if (!name.isNullOrBlank()) {
                        fileName = name
                    }
                }
            }
        }
        return fileName
    }

    private fun processDeleteMusic(musicItem: MusicItem) {
        viewModelScope.launch {
            val file = File(musicItem.uri!!)
            if (file.exists()) {
                file.delete()
            }
            musicSettingUseCase.deleteMusicPath(musicItem.id!!)
        }
    }
}