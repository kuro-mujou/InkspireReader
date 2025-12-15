package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.data.preference.AppPreferences
import com.inkspire.ebookreader.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow

class AppPreferencesRepositoryImpl(private val appPreferences: AppPreferences) : AppPreferencesRepository {
    override fun getKeepScreenOn(): Flow<Boolean> {
        return appPreferences.getKeepScreenOn
    }
    override fun getTtsSpeed(): Flow<Float> {
        return appPreferences.getTtsSpeed
    }
    override fun getTtsPitch(): Flow<Float> {
        return appPreferences.getTtsPitch
    }
    override fun getTtsLocale(): Flow<String> {
        return appPreferences.getTtsLocale
    }
    override fun getTtsVoice(): Flow<String> {
        return appPreferences.getTtsVoice
    }
    override fun getAutoScrollSpeed(): Flow<Int> {
        return appPreferences.getAutoScrollSpeed
    }
    override fun getDelayTimeAtStart(): Flow<Int> {
        return appPreferences.getDelayTimeAtStart
    }
    override fun getDelayTimeAtEnd(): Flow<Int> {
        return appPreferences.getDelayTimeAtEnd
    }
    override fun getAutoScrollResumeMode(): Flow<Boolean> {
        return appPreferences.getAutoScrollResumeMode
    }
    override fun getAutoScrollResumeDelayTime(): Flow<Int> {
        return appPreferences.getAutoScrollResumeDelayTime
    }
    override fun getBackgroundColor(): Flow<Int> {
        return appPreferences.getBackgroundColor
    }
    override fun getTextColor(): Flow<Int> {
        return appPreferences.getTextColor
    }
    override fun getSelectedColorSet(): Flow<Int> {
        return appPreferences.getSelectedColorSet
    }
    override fun getFontSize(): Flow<Int> {
        return appPreferences.getFontSize
    }
    override fun getTextAlign(): Flow<Boolean> {
        return appPreferences.getTextAlign
    }
    override fun getTextIndent(): Flow<Boolean> {
        return appPreferences.getTextIndent
    }
    override fun getLineSpacing(): Flow<Int> {
        return appPreferences.getLineSpacing
    }
    override fun getFontFamily(): Flow<Int> {
        return appPreferences.getFontFamily
    }
    override fun getIsSortedByFavorite(): Flow<Boolean> {
        return appPreferences.getIsSortedByFavorite
    }
    override fun getEnableBackgroundMusic(): Flow<Boolean> {
        return appPreferences.getEnableBackgroundMusic
    }
    override fun getPlayerVolume(): Flow<Float> {
        return appPreferences.getPlayerVolume
    }
    override fun getBookListViewType(): Flow<Int> {
        return appPreferences.getBookListViewType
    }
    override fun getImagePaddingState(): Flow<Boolean> {
        return appPreferences.getImagePaddingState
    }
    override fun getBookmarkStyle(): Flow<BookmarkStyle> {
        return appPreferences.getBookmarkStyle
    }
    override fun getUnlockSpecialCodeStatus(): Flow<Boolean> {
        return appPreferences.getUnlockSpecialCodeStatus
    }
    override fun getEnableSpecialArt(): Flow<Boolean> {
        return appPreferences.getEnableSpecialArt
    }

    override suspend fun setKeepScreenOn(value: Boolean) {
        appPreferences.setKeepScreenOn(value)
    }

    override suspend fun setTTSSpeed(value: Float) {
        appPreferences.setTTSSpeed(value)
    }

    override suspend fun setTTSPitch(value: Float) {
        appPreferences.setTTSPitch(value)
    }

    override suspend fun setTTSLocale(value: String) {
        appPreferences.setTTSLocale(value)
    }

    override suspend fun setTTSVoice(value: String) {
        appPreferences.setTTSVoice(value)
    }

    override suspend fun setAutoScrollSpeed(value: Int) {
        appPreferences.setAutoScrollSpeed(value)
    }

    override suspend fun setDelayTimeAtStart(value: Int) {
        appPreferences.setDelayTimeAtStart(value)
    }

    override suspend fun setDelayTimeAtEnd(value: Int) {
        appPreferences.setDelayTimeAtEnd(value)
    }

    override suspend fun setAutoScrollResumeMode(value: Boolean) {
        appPreferences.setAutoScrollResumeMode(value)
    }

    override suspend fun setAutoScrollResumeDelayTime(value: Int) {
        appPreferences.setAutoScrollResumeDelayTime(value)
    }

    override suspend fun setBackgroundColor(value: Int) {
        appPreferences.setBackgroundColor(value)
    }

    override suspend fun setTextColor(value: Int) {
        appPreferences.setTextColor(value)
    }

    override suspend fun setSelectedColorSet(value: Int) {
        appPreferences.setSelectedColorSet(value)
    }

    override suspend fun setFontSize(value: Int) {
        appPreferences.setFontSize(value)
    }

    override suspend fun setTextAlign(value: Boolean) {
        appPreferences.setTextAlign(value)
    }

    override suspend fun setTextIndent(value: Boolean) {
        appPreferences.setTextIndent(value)
    }

    override suspend fun setLineSpacing(value: Int) {
        appPreferences.setLineSpacing(value)
    }

    override suspend fun setFontFamily(value: Int) {
        appPreferences.setFontFamily(value)
    }

    override suspend fun setSortByFavorite(value: Boolean) {
        appPreferences.setSortByFavorite(value)
    }

    override suspend fun setEnableBackgroundMusic(value: Boolean) {
        appPreferences.setEnableBackgroundMusic(value)
    }

    override suspend fun setPlayerVolume(value: Float) {
        appPreferences.setPlayerVolume(value)
    }

    override suspend fun setBookListView(value: Int) {
        appPreferences.setBookListView(value)
    }

    override suspend fun setImagePaddingState(value: Boolean) {
        appPreferences.setImagePaddingState(value)
    }

    override suspend fun setBookmarkStyle(bookmarkStyle: BookmarkStyle) {
        appPreferences.setBookmarkStyle(bookmarkStyle)
    }

    override suspend fun setUnlockSpecialCodeStatus(value: Boolean) {
        appPreferences.setUnlockSpecialCodeStatus(value)
    }

    override suspend fun setEnableSpecialArt(value: Boolean) {
        appPreferences.setEnableSpecialArt(value)
    }
}
