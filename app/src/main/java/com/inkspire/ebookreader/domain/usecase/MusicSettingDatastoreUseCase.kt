package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepo

class MusicSettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepo
) {
    val musicPreferences = datastoreRepository.musicPreferences
    suspend fun setEnableBackgroundMusic(value: Boolean) = datastoreRepository.setEnableBackgroundMusic(value)
    suspend fun setPlayerVolume(value: Float) = datastoreRepository.setPlayerVolume(value)
}