package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class TTSDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    val ttsPreferences = datastoreRepository.ttsPreferences
    val musicPreferences = datastoreRepository.musicPreferences
}