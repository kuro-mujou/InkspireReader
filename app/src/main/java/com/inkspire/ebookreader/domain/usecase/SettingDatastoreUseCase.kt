package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class SettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getKeepScreenOn() = datastoreRepository.getKeepScreenOn()
    fun getEnableSpecialArt() = datastoreRepository.getEnableSpecialArt()
    fun getUnlockSpecialCodeStatus() = datastoreRepository.getUnlockSpecialCodeStatus()

    suspend fun setEnableSpecialArt(enable: Boolean) = datastoreRepository.setEnableSpecialArt(enable)
    suspend fun setUnlockSpecialCodeStatus(status: Boolean) = datastoreRepository.setUnlockSpecialCodeStatus(status)
    suspend fun setKeepScreenOn(value: Boolean) = datastoreRepository.setKeepScreenOn(value)
}