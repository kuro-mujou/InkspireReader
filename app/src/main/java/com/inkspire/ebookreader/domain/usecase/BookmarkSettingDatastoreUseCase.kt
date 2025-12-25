package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class BookmarkSettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getBookmarkStyle() = datastoreRepository.getBookmarkStyle()
    suspend fun setBookmarkStyle(value: BookmarkStyle) = datastoreRepository.setBookmarkStyle(value)
}