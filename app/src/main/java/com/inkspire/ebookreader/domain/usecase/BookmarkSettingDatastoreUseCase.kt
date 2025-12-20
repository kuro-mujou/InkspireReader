package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class BookmarkSettingDatastoreUseCase(
    private val dataStoreRepository: DatastoreRepository
) {
    fun getBookmarkStyle() = dataStoreRepository.getBookmarkStyle()
    suspend fun setBookmarkStyle(value: BookmarkStyle) = dataStoreRepository.setBookmarkStyle(value)
}