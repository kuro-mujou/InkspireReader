package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class LibraryDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    val librarySettings = datastoreRepository.librarySettingPreferences
    suspend fun setBookListViewType(value: Int) = datastoreRepository.setBookListView(value)
    suspend fun setSortByFavorite(value: Boolean) = datastoreRepository.setSortByFavorite(value)

}