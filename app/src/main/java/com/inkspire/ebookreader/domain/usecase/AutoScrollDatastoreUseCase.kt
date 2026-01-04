package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class AutoScrollDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    val autoScrollPreferences = datastoreRepository.autoScrollPreferences
}