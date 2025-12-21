package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class TTSSettingDataStoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getTtsLocale() = datastoreRepository.getTtsLocale()
    fun getTtsSpeed() = datastoreRepository.getTtsSpeed()
    fun getTtsPitch() = datastoreRepository.getTtsPitch()
    fun getTtsVoice() = datastoreRepository.getTtsVoice()

    suspend fun setTTSLocale(value: String) = datastoreRepository.setTTSLocale(value)
    suspend fun setTTSVoice(value: String) = datastoreRepository.setTTSVoice(value)
    suspend fun setTTSSpeed(value: Float) = datastoreRepository.setTTSSpeed(value)
    suspend fun setTTSPitch(value: Float) = datastoreRepository.setTTSPitch(value)
}