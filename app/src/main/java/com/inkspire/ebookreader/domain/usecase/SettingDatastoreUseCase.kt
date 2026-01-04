package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class SettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    val readerSettings = datastoreRepository.readerSettingPreferences
    suspend fun setKeepScreenOn(value: Boolean) = datastoreRepository.setKeepScreenOn(value)
}