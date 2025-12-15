package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.common.BookmarkStyle
import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    fun getKeepScreenOn(): Flow<Boolean>
    fun getTtsSpeed(): Flow<Float>
    fun getTtsPitch(): Flow<Float>
    fun getTtsLocale(): Flow<String>
    fun getTtsVoice(): Flow<String>
    fun getAutoScrollSpeed(): Flow<Int>
    fun getDelayTimeAtStart(): Flow<Int>
    fun getDelayTimeAtEnd(): Flow<Int>
    fun getAutoScrollResumeMode(): Flow<Boolean>
    fun getAutoScrollResumeDelayTime(): Flow<Int>
    fun getBackgroundColor(): Flow<Int>
    fun getTextColor(): Flow<Int>
    fun getSelectedColorSet(): Flow<Int>
    fun getFontSize(): Flow<Int>
    fun getTextAlign(): Flow<Boolean>
    fun getTextIndent(): Flow<Boolean>
    fun getLineSpacing(): Flow<Int>
    fun getFontFamily(): Flow<Int>
    fun getIsSortedByFavorite(): Flow<Boolean>
    fun getEnableBackgroundMusic(): Flow<Boolean>
    fun getPlayerVolume(): Flow<Float>
    fun getBookListViewType(): Flow<Int>
    fun getImagePaddingState(): Flow<Boolean>
    fun getBookmarkStyle(): Flow<BookmarkStyle>
    fun getUnlockSpecialCodeStatus(): Flow<Boolean>
    fun getEnableSpecialArt(): Flow<Boolean>

    suspend fun setKeepScreenOn(value: Boolean)
    suspend fun setTTSSpeed(value: Float)
    suspend fun setTTSPitch(value: Float)
    suspend fun setTTSLocale(value: String)
    suspend fun setTTSVoice(value: String)
    suspend fun setAutoScrollSpeed(value: Int)
    suspend fun setDelayTimeAtStart(value: Int)
    suspend fun setDelayTimeAtEnd(value: Int)
    suspend fun setAutoScrollResumeMode(value: Boolean)
    suspend fun setAutoScrollResumeDelayTime(value: Int)
    suspend fun setBackgroundColor(value: Int)
    suspend fun setTextColor(value: Int)
    suspend fun setSelectedColorSet(value: Int)
    suspend fun setFontSize(value: Int)
    suspend fun setTextAlign(value: Boolean)
    suspend fun setTextIndent(value: Boolean)
    suspend fun setLineSpacing(value: Int)
    suspend fun setFontFamily(value: Int)
    suspend fun setSortByFavorite(value: Boolean)
    suspend fun setEnableBackgroundMusic(value: Boolean)
    suspend fun setPlayerVolume(value: Float)
    suspend fun setBookListView(value: Int)
    suspend fun setImagePaddingState(value: Boolean)
    suspend fun setBookmarkStyle(bookmarkStyle: BookmarkStyle)
    suspend fun setUnlockSpecialCodeStatus(value: Boolean)
    suspend fun setEnableSpecialArt(value: Boolean)
}
