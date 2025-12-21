package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class BookContentStylingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getTextColor() = datastoreRepository.getTextColor()
    fun getBackgroundColor() = datastoreRepository.getBackgroundColor()
    fun getSelectedColorSet() = datastoreRepository.getSelectedColorSet()
    fun getFontSize() = datastoreRepository.getFontSize()
    fun getFontFamily() = datastoreRepository.getFontFamily()
    fun getLineSpacing() = datastoreRepository.getLineSpacing()
    fun getTextAlign() = datastoreRepository.getTextAlign()
    fun getTextIndent() = datastoreRepository.getTextIndent()
    fun getImagePaddingState() = datastoreRepository.getImagePaddingState()

    suspend fun setTextColor(color: Int) = datastoreRepository.setTextColor(color)
    suspend fun setBackgroundColor(color: Int) = datastoreRepository.setBackgroundColor(color)
    suspend fun setSelectedColorSet(colorSetIndex: Int) = datastoreRepository.setSelectedColorSet(colorSetIndex)
    suspend fun setFontSize(fontSize: Int) = datastoreRepository.setFontSize(fontSize)
    suspend fun setFontFamily(fontFamily: Int) = datastoreRepository.setFontFamily(fontFamily)
    suspend fun setLineSpacing(lineSpacing: Int) = datastoreRepository.setLineSpacing(lineSpacing)
    suspend fun setTextAlign(textAlign: Boolean) = datastoreRepository.setTextAlign(textAlign)
    suspend fun setTextIndent(textIndent: Boolean) = datastoreRepository.setTextIndent(textIndent)
    suspend fun setImagePaddingState(value: Boolean) = datastoreRepository.setImagePaddingState(value)
}