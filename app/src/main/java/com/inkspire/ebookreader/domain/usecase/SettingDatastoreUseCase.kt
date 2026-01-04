package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepo

class SettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepo
) {
    val readerSettings = datastoreRepository.readerSettingPreferences
    suspend fun setKeepScreenOn(value: Boolean) = datastoreRepository.setKeepScreenOn(value)
}