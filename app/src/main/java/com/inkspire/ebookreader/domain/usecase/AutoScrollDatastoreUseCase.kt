package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepo

class AutoScrollDatastoreUseCase(
    private val datastoreRepository: DatastoreRepo
) {
    val autoScrollPreferences = datastoreRepository.autoScrollPreferences
}