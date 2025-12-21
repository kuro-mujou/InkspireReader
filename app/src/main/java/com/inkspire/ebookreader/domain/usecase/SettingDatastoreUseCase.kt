package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class SettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getKeepScreenOn() = datastoreRepository.getKeepScreenOn()
    suspend fun setKeepScreenOn(value: Boolean) = datastoreRepository.setKeepScreenOn(value)
}