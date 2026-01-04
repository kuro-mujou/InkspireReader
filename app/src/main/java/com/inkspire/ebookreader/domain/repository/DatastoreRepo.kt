package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.domain.model.AutoScrollPreferences
import com.inkspire.ebookreader.domain.model.LibrarySettingPreferences
import com.inkspire.ebookreader.domain.model.MusicPreferences
import com.inkspire.ebookreader.domain.model.ReaderSettingPreferences
import com.inkspire.ebookreader.domain.model.StylePreferences
import com.inkspire.ebookreader.domain.model.TTSPreferences
import kotlinx.coroutines.flow.Flow

interface DatastoreRepo {
    val ttsPreferences: Flow<TTSPreferences>
    val autoScrollPreferences: Flow<AutoScrollPreferences>
    val musicPreferences: Flow<MusicPreferences>
    val stylePreferences: Flow<StylePreferences>
    val readerSettingPreferences: Flow<ReaderSettingPreferences>
    val librarySettingPreferences: Flow<LibrarySettingPreferences>

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
}