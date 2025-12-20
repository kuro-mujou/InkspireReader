package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class MusicSettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getEnableBackgroundMusic() = datastoreRepository.getEnableBackgroundMusic()
    fun getPlayerVolume() = datastoreRepository.getPlayerVolume()
    suspend fun setEnableBackgroundMusic(value: Boolean) = datastoreRepository.setEnableBackgroundMusic(value)
    suspend fun setPlayerVolume(value: Float) = datastoreRepository.setPlayerVolume(value)
}