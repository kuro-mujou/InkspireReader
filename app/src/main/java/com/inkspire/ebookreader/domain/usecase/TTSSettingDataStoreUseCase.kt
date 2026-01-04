package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepo

class TTSSettingDataStoreUseCase(
    private val datastoreRepository: DatastoreRepo
) {
    suspend fun setTTSLocale(value: String) = datastoreRepository.setTTSLocale(value)
    suspend fun setTTSVoice(value: String) = datastoreRepository.setTTSVoice(value)
    suspend fun setTTSSpeed(value: Float) = datastoreRepository.setTTSSpeed(value)
    suspend fun setTTSPitch(value: Float) = datastoreRepository.setTTSPitch(value)
}