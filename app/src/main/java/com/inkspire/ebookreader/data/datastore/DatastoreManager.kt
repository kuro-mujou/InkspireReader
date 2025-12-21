package com.inkspire.ebookreader.data.datastore

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.inkspire.ebookreader.common.BookmarkStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

private const val DATASTORE_NAME = "user_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DatastoreManager(private val context: Context) {
    companion object Keys {
        private val KEEP_SCREEN_ON = booleanPreferencesKey("KEEP_SCREEN_ON")
        private val TTS_SPEED = floatPreferencesKey("TTS_SPEED")
        private val TTS_PITCH = floatPreferencesKey("TTS_PITCH")
        private val TTS_LOCALE = stringPreferencesKey("TTS_LOCALE")
        private val TTS_VOICE = stringPreferencesKey("TTS_VOICE")
        private val AUTO_SCROLL_SPEED = intPreferencesKey("AUTO_SCROLL_SPEED")
        private val DELAY_TIME_AT_START = intPreferencesKey("DELAY_TIME_AT_START")
        private val DELAY_TIME_AT_END = intPreferencesKey("DELAY_TIME_AT_END")
        private val AUTO_SCROLL_RESUME_MODE = booleanPreferencesKey("AUTO_SCROLL_RESUME_MODE")
        private val AUTO_SCROLL_RESUME_DELAY_TIME = intPreferencesKey("AUTO_SCROLL_RESUME_DELAY_TIME")
        private val BACKGROUND_COLOR = intPreferencesKey("BACKGROUND_COLOR")
        private val TEXT_COLOR = intPreferencesKey("TEXT_COLOR")
        private val SELECTED_COLOR_SET = intPreferencesKey("SELECTED_COLOR_SET")
        private val FONT_SIZE = intPreferencesKey("FONT_SIZE")
        private val TEXT_ALIGN = booleanPreferencesKey("TEXT_ALIGN")
        private val TEXT_INDENT = booleanPreferencesKey("TEXT_INDENT")
        private val LINE_SPACING = intPreferencesKey("LINE_SPACING")
        private val FONT_FAMILY = intPreferencesKey("FONT_FAMILY")
        private val IS_SORTED_BY_FAVORITE = booleanPreferencesKey("IS_SORTED_BY_FAVORITE")
        private val ENABLE_BACKGROUND_MUSIC = booleanPreferencesKey("ENABLE_BACKGROUND_MUSIC")
        private val PLAYER_VOLUME = floatPreferencesKey("PLAYER_VOLUME")
        private val BOOK_LIST_VIEW_TYPE = intPreferencesKey("BOOK_LIST_VIEW_TYPE")
        private val IMAGE_PADDING_STATE = booleanPreferencesKey("IMAGE_PADDING_STATE")
        private val BOOKMARK_STYLE = stringPreferencesKey("BOOKMARK_STYLE")
    }

    val getKeepScreenOn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEEP_SCREEN_ON] == true
    }
    val getTtsSpeed: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[TTS_SPEED] ?: 1f
    }
    val getTtsPitch: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[TTS_PITCH] ?: 1f
    }
    val getTtsLocale: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TTS_LOCALE] ?: Locale.getDefault().displayName
    }
    val getTtsVoice: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TTS_VOICE] ?: ""
    }
    val getAutoScrollSpeed: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[AUTO_SCROLL_SPEED] ?: 10000
    }
    val getDelayTimeAtStart: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DELAY_TIME_AT_START] ?: 3000
    }
    val getDelayTimeAtEnd: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DELAY_TIME_AT_END] ?: 3000
    }
    val getAutoScrollResumeMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_SCROLL_RESUME_MODE] == true
    }
    val getAutoScrollResumeDelayTime: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[AUTO_SCROLL_RESUME_DELAY_TIME] ?: 2000
    }
    val getBackgroundColor: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[BACKGROUND_COLOR] ?: Color(0xFFD3C3A3).toArgb()
    }
    val getTextColor: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TEXT_COLOR] ?: Color(0xFF3A3129).toArgb()
    }
    val getSelectedColorSet: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_COLOR_SET] ?: 0
    }
    val getFontSize: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FONT_SIZE] ?: 20
    }
    val getTextAlign: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[TEXT_ALIGN] != false
    }
    val getTextIndent: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[TEXT_INDENT] != false
    }
    val getLineSpacing: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LINE_SPACING] ?: 14
    }
    val getFontFamily: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FONT_FAMILY] ?: 0
    }
    val getIsSortedByFavorite: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_SORTED_BY_FAVORITE] != false
    }
    val getEnableBackgroundMusic: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_BACKGROUND_MUSIC] == true
    }
    val getPlayerVolume: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PLAYER_VOLUME] ?: 1f
    }
    val getBookListViewType: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[BOOK_LIST_VIEW_TYPE] ?: 1
    }
    val getImagePaddingState: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IMAGE_PADDING_STATE] != false
    }
    val getBookmarkStyle: Flow<BookmarkStyle> = context.dataStore.data.map { preferences ->
        val raw = preferences[BOOKMARK_STYLE] ?: BookmarkStyle.WAVE_WITH_BIRDS.name
        try {
            BookmarkStyle.valueOf(raw)
        } catch (_: IllegalArgumentException) {
            BookmarkStyle.WAVE_WITH_BIRDS
        }
    }

    suspend fun setKeepScreenOn(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEEP_SCREEN_ON] = value
        }
    }

    suspend fun setTTSSpeed(value: Float) {
        context.dataStore.edit { preferences ->
            preferences[TTS_SPEED] = value
        }
    }

    suspend fun setTTSPitch(value: Float) {
        context.dataStore.edit { preferences ->
            preferences[TTS_PITCH] = value
        }
    }

    suspend fun setTTSLocale(value: String) {
        context.dataStore.edit { preferences ->
            preferences[TTS_LOCALE] = value
        }
    }

    suspend fun setTTSVoice(value: String) {
        context.dataStore.edit { preferences ->
            preferences[TTS_VOICE] = value
        }
    }

    suspend fun setAutoScrollSpeed(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_SCROLL_SPEED] = value
        }
    }

    suspend fun setDelayTimeAtStart(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[DELAY_TIME_AT_START] = value
        }
    }

    suspend fun setDelayTimeAtEnd(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[DELAY_TIME_AT_END] = value
        }
    }

    suspend fun setAutoScrollResumeMode(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_SCROLL_RESUME_MODE] = value
        }
    }

    suspend fun setAutoScrollResumeDelayTime(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_SCROLL_RESUME_DELAY_TIME] = value
        }
    }

    suspend fun setBackgroundColor(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[BACKGROUND_COLOR] = value
        }
    }

    suspend fun setTextColor(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[TEXT_COLOR] = value
        }
    }

    suspend fun setSelectedColorSet(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_COLOR_SET] = value
        }
    }

    suspend fun setFontSize(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE] = value
        }
    }

    suspend fun setTextAlign(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TEXT_ALIGN] = value
        }
    }

    suspend fun setTextIndent(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TEXT_INDENT] = value
        }
    }

    suspend fun setLineSpacing(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[LINE_SPACING] = value
        }
    }

    suspend fun setFontFamily(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[FONT_FAMILY] = value
        }
    }

    suspend fun setSortByFavorite(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SORTED_BY_FAVORITE] = value
        }
    }

    suspend fun setEnableBackgroundMusic(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ENABLE_BACKGROUND_MUSIC] = value
        }
    }

    suspend fun setPlayerVolume(value: Float) {
        context.dataStore.edit { preferences ->
            preferences[PLAYER_VOLUME] = value
        }
    }

    suspend fun setBookListView(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[BOOK_LIST_VIEW_TYPE] = value
        }
    }

    suspend fun setImagePaddingState(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IMAGE_PADDING_STATE] = value
        }
    }

    suspend fun setBookmarkStyle(bookmarkStyle: BookmarkStyle) {
        context.dataStore.edit { preferences ->
            preferences[BOOKMARK_STYLE] = bookmarkStyle.name
        }
    }
}