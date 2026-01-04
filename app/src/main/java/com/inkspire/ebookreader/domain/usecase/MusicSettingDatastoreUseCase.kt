package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class MusicSettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    val musicPreferences = datastoreRepository.musicPreferences
    suspend fun setEnableBackgroundMusic(value: Boolean) = datastoreRepository.setEnableBackgroundMusic(value)
    suspend fun setPlayerVolume(value: Float) = datastoreRepository.setPlayerVolume(value)
    suspend fun setEnableOnlyRunWithTTS(value: Boolean) = datastoreRepository.setEnableOnlyRunWithTTS(value)
}