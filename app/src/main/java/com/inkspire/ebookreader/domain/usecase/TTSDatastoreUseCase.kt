package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class TTSDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    val ttsPreferences = datastoreRepository.ttsPreferences
    val musicPreferences = datastoreRepository.musicPreferences
    suspend fun setTTSLocale(value: String) = datastoreRepository.setTTSLocale(value)
    suspend fun setTTSVoice(value: String) = datastoreRepository.setTTSVoice(value)
}