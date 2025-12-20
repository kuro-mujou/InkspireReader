package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class AutoScrollSettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getAutoScrollSpeed() = datastoreRepository.getAutoScrollSpeed()
    fun getDelayTimeAtStart() = datastoreRepository.getDelayTimeAtStart()
    fun getDelayTimeAtEnd() = datastoreRepository.getDelayTimeAtEnd()
    fun getAutoScrollResumeDelayTime() = datastoreRepository.getAutoScrollResumeDelayTime()
    fun getAutoScrollResumeMode() = datastoreRepository.getAutoScrollResumeMode()

    suspend fun setAutoScrollSpeed(value: Int) = datastoreRepository.setAutoScrollSpeed(value)
    suspend fun setAutoScrollResumeDelayTime(value: Int) = datastoreRepository.setAutoScrollResumeDelayTime(value)
    suspend fun setDelayTimeAtStart(value: Int) = datastoreRepository.setDelayTimeAtStart(value)
    suspend fun setDelayTimeAtEnd(value: Int) = datastoreRepository.setDelayTimeAtEnd(value)
    suspend fun setAutoScrollResumeMode(value: Boolean) = datastoreRepository.setAutoScrollResumeMode(value)
}