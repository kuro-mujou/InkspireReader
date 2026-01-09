package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class BookContentStylingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    val stylePreferences = datastoreRepository.stylePreferences
    suspend fun setTextColor(color: Int) = datastoreRepository.setTextColor(color)
    suspend fun setBackgroundColor(color: Int) = datastoreRepository.setBackgroundColor(color)
    suspend fun setSelectedColorSet(colorSetIndex: Int) = datastoreRepository.setSelectedColorSet(colorSetIndex)
    suspend fun setFontSize(fontSize: Int) = datastoreRepository.setFontSize(fontSize)
    suspend fun setFontFamily(fontFamily: Int) = datastoreRepository.setFontFamily(fontFamily)
    suspend fun setLineSpacing(lineSpacing: Int) = datastoreRepository.setLineSpacing(lineSpacing)
    suspend fun setTextHighlight(textHighlight: Boolean) = datastoreRepository.setTextHighlight(textHighlight)
    suspend fun setTextIndent(textIndent: Boolean) = datastoreRepository.setTextIndent(textIndent)
    suspend fun setImagePaddingState(value: Boolean) = datastoreRepository.setImagePaddingState(value)
}