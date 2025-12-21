package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.data.datastore.DatastoreManager
import com.inkspire.ebookreader.domain.repository.DatastoreRepository
import kotlinx.coroutines.flow.Flow

class DatastoreRepositoryImpl(private val datastoreManager: DatastoreManager) : DatastoreRepository {
    override fun getKeepScreenOn(): Flow<Boolean> {
        return datastoreManager.getKeepScreenOn
    }
    override fun getTtsSpeed(): Flow<Float> {
        return datastoreManager.getTtsSpeed
    }
    override fun getTtsPitch(): Flow<Float> {
        return datastoreManager.getTtsPitch
    }
    override fun getTtsLocale(): Flow<String> {
        return datastoreManager.getTtsLocale
    }
    override fun getTtsVoice(): Flow<String> {
        return datastoreManager.getTtsVoice
    }
    override fun getAutoScrollSpeed(): Flow<Int> {
        return datastoreManager.getAutoScrollSpeed
    }
    override fun getDelayTimeAtStart(): Flow<Int> {
        return datastoreManager.getDelayTimeAtStart
    }
    override fun getDelayTimeAtEnd(): Flow<Int> {
        return datastoreManager.getDelayTimeAtEnd
    }
    override fun getAutoScrollResumeMode(): Flow<Boolean> {
        return datastoreManager.getAutoScrollResumeMode
    }
    override fun getAutoScrollResumeDelayTime(): Flow<Int> {
        return datastoreManager.getAutoScrollResumeDelayTime
    }
    override fun getBackgroundColor(): Flow<Int> {
        return datastoreManager.getBackgroundColor
    }
    override fun getTextColor(): Flow<Int> {
        return datastoreManager.getTextColor
    }
    override fun getSelectedColorSet(): Flow<Int> {
        return datastoreManager.getSelectedColorSet
    }
    override fun getFontSize(): Flow<Int> {
        return datastoreManager.getFontSize
    }
    override fun getTextAlign(): Flow<Boolean> {
        return datastoreManager.getTextAlign
    }
    override fun getTextIndent(): Flow<Boolean> {
        return datastoreManager.getTextIndent
    }
    override fun getLineSpacing(): Flow<Int> {
        return datastoreManager.getLineSpacing
    }
    override fun getFontFamily(): Flow<Int> {
        return datastoreManager.getFontFamily
    }
    override fun getIsSortedByFavorite(): Flow<Boolean> {
        return datastoreManager.getIsSortedByFavorite
    }
    override fun getEnableBackgroundMusic(): Flow<Boolean> {
        return datastoreManager.getEnableBackgroundMusic
    }
    override fun getPlayerVolume(): Flow<Float> {
        return datastoreManager.getPlayerVolume
    }
    override fun getBookListViewType(): Flow<Int> {
        return datastoreManager.getBookListViewType
    }
    override fun getImagePaddingState(): Flow<Boolean> {
        return datastoreManager.getImagePaddingState
    }
    override fun getBookmarkStyle(): Flow<BookmarkStyle> {
        return datastoreManager.getBookmarkStyle
    }

    override suspend fun setKeepScreenOn(value: Boolean) {
        datastoreManager.setKeepScreenOn(value)
    }

    override suspend fun setTTSSpeed(value: Float) {
        datastoreManager.setTTSSpeed(value)
    }

    override suspend fun setTTSPitch(value: Float) {
        datastoreManager.setTTSPitch(value)
    }

    override suspend fun setTTSLocale(value: String) {
        datastoreManager.setTTSLocale(value)
    }

    override suspend fun setTTSVoice(value: String) {
        datastoreManager.setTTSVoice(value)
    }

    override suspend fun setAutoScrollSpeed(value: Int) {
        datastoreManager.setAutoScrollSpeed(value)
    }

    override suspend fun setDelayTimeAtStart(value: Int) {
        datastoreManager.setDelayTimeAtStart(value)
    }

    override suspend fun setDelayTimeAtEnd(value: Int) {
        datastoreManager.setDelayTimeAtEnd(value)
    }

    override suspend fun setAutoScrollResumeMode(value: Boolean) {
        datastoreManager.setAutoScrollResumeMode(value)
    }

    override suspend fun setAutoScrollResumeDelayTime(value: Int) {
        datastoreManager.setAutoScrollResumeDelayTime(value)
    }

    override suspend fun setBackgroundColor(value: Int) {
        datastoreManager.setBackgroundColor(value)
    }

    override suspend fun setTextColor(value: Int) {
        datastoreManager.setTextColor(value)
    }

    override suspend fun setSelectedColorSet(value: Int) {
        datastoreManager.setSelectedColorSet(value)
    }

    override suspend fun setFontSize(value: Int) {
        datastoreManager.setFontSize(value)
    }

    override suspend fun setTextAlign(value: Boolean) {
        datastoreManager.setTextAlign(value)
    }

    override suspend fun setTextIndent(value: Boolean) {
        datastoreManager.setTextIndent(value)
    }

    override suspend fun setLineSpacing(value: Int) {
        datastoreManager.setLineSpacing(value)
    }

    override suspend fun setFontFamily(value: Int) {
        datastoreManager.setFontFamily(value)
    }

    override suspend fun setSortByFavorite(value: Boolean) {
        datastoreManager.setSortByFavorite(value)
    }

    override suspend fun setEnableBackgroundMusic(value: Boolean) {
        datastoreManager.setEnableBackgroundMusic(value)
    }

    override suspend fun setPlayerVolume(value: Float) {
        datastoreManager.setPlayerVolume(value)
    }

    override suspend fun setBookListView(value: Int) {
        datastoreManager.setBookListView(value)
    }

    override suspend fun setImagePaddingState(value: Boolean) {
        datastoreManager.setImagePaddingState(value)
    }

    override suspend fun setBookmarkStyle(bookmarkStyle: BookmarkStyle) {
        datastoreManager.setBookmarkStyle(bookmarkStyle)
    }
}
