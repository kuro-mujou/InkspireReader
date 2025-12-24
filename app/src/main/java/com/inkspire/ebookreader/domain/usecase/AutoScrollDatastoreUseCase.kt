package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class AutoScrollDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getAutoScrollSpeed() = datastoreRepository.getAutoScrollSpeed()
    fun getDelayTimeAtStart() = datastoreRepository.getDelayTimeAtStart()
    fun getDelayTimeAtEnd() = datastoreRepository.getDelayTimeAtEnd()
    fun getAutoScrollResumeDelayTime() = datastoreRepository.getAutoScrollResumeDelayTime()
    fun getAutoScrollResumeMode() = datastoreRepository.getAutoScrollResumeMode()
}