package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepo

class TTSDatastoreUseCase(
    private val datastoreRepository: DatastoreRepo
) {
    val ttsPreferences = datastoreRepository.ttsPreferences
    val musicPreferences = datastoreRepository.musicPreferences
}