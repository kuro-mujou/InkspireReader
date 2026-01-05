package com.inkspire.ebookreader.data.repository

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
import com.inkspire.ebookreader.domain.model.AutoScrollPreferences
import com.inkspire.ebookreader.domain.model.LibrarySettingPreferences
import com.inkspire.ebookreader.domain.model.MusicPreferences
import com.inkspire.ebookreader.domain.model.ReaderSettingPreferences
import com.inkspire.ebookreader.domain.model.StylePreferences
import com.inkspire.ebookreader.domain.model.TTSPreferences
import com.inkspire.ebookreader.domain.repository.DatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Locale


private val Context.datastore by preferencesDataStore("user_preferences")
class DatastoreRepositoryImpl(private val context: Context) : DatastoreRepository {
    companion object Keys {
        private val KEEP_SCREEN_ON = booleanPreferencesKey("KEEP_SCREEN_ON")
        private val BOOKMARK_STYLE = stringPreferencesKey("BOOKMARK_STYLE")

        private val TTS_SPEED = floatPreferencesKey("TTS_SPEED")
        private val TTS_PITCH = floatPreferencesKey("TTS_PITCH")
        private val TTS_LOCALE = stringPreferencesKey("TTS_LOCALE")
        private val TTS_VOICE = stringPreferencesKey("TTS_VOICE")

        private val ENABLE_BACKGROUND_MUSIC = booleanPreferencesKey("ENABLE_BACKGROUND_MUSIC")
        private val ONLY_RUN_WITH_TTS = booleanPreferencesKey("ONLY_RUN_WITH_TTS")
        private val PLAYER_VOLUME = floatPreferencesKey("PLAYER_VOLUME")

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
        private val IMAGE_PADDING_STATE = booleanPreferencesKey("IMAGE_PADDING_STATE")

        private val IS_SORTED_BY_FAVORITE = booleanPreferencesKey("IS_SORTED_BY_FAVORITE")
        private val BOOK_LIST_VIEW_TYPE = intPreferencesKey("BOOK_LIST_VIEW_TYPE")
    }
    override val ttsPreferences: Flow<TTSPreferences> = context.datastore.data
        .map { prefs ->
            TTSPreferences(
                locale = prefs[TTS_LOCALE] ?: Locale.getDefault().displayName,
                voice = prefs[TTS_VOICE] ?: "",
                speed = prefs[TTS_SPEED] ?: 1f,
                pitch = prefs[TTS_PITCH] ?: 1f
            )
        }
        .distinctUntilChanged()

    override val autoScrollPreferences: Flow<AutoScrollPreferences> = context.datastore.data
        .map { prefs ->
            AutoScrollPreferences(
                speed = prefs[AUTO_SCROLL_SPEED] ?: 20000,
                delayTimeAtStart = prefs[DELAY_TIME_AT_START] ?: 3000,
                delayTimeAtEnd = prefs[DELAY_TIME_AT_END] ?: 3000,
                resumeMode = prefs[AUTO_SCROLL_RESUME_MODE] ?: false,
                resumeDelay = prefs[AUTO_SCROLL_RESUME_DELAY_TIME] ?: 2000
            )
        }
        .distinctUntilChanged()

    override val musicPreferences: Flow<MusicPreferences> = context.datastore.data
        .map { prefs ->
            MusicPreferences(
                enable = prefs[ENABLE_BACKGROUND_MUSIC] ?: false,
                onlyRunWithTTS = prefs[ONLY_RUN_WITH_TTS] ?: true,
                volume = prefs[PLAYER_VOLUME] ?: 1f
            )
        }
        .distinctUntilChanged()

    override val stylePreferences: Flow<StylePreferences> = context.datastore.data
        .map { prefs ->
            StylePreferences(
                backgroundColor = Color(prefs[BACKGROUND_COLOR] ?: Color(0xFFF1F7ED).toArgb()),
                textColor = Color(prefs[TEXT_COLOR] ?: Color(0xFF1B310E).toArgb()),
                selectedColorSet = prefs[SELECTED_COLOR_SET] ?: 0,
                fontSize = prefs[FONT_SIZE] ?: 20,
                textAlign = prefs[TEXT_ALIGN] ?: true,
                textIndent = prefs[TEXT_INDENT] ?: true,
                lineSpacing = prefs[LINE_SPACING] ?: 14,
                fontFamily = prefs[FONT_FAMILY] ?: 0,
                imagePaddingState = prefs[IMAGE_PADDING_STATE] ?: false
            )
        }

    override val librarySettingPreferences: Flow<LibrarySettingPreferences> = context.datastore.data
        .map { prefs ->
            LibrarySettingPreferences(
                isSortedByFavorite = prefs[IS_SORTED_BY_FAVORITE] ?: false,
                bookListViewType = prefs[BOOK_LIST_VIEW_TYPE] ?: 1
            )
        }
        .distinctUntilChanged()

    override val readerSettingPreferences: Flow<ReaderSettingPreferences> = context.datastore.data
        .map { prefs ->
            ReaderSettingPreferences(
                keepScreenOn = prefs[KEEP_SCREEN_ON] ?: false,
                bookmarkStyle = BookmarkStyle.valueOf(prefs[BOOKMARK_STYLE] ?: BookmarkStyle.WAVE_WITH_BIRDS.name)
            )
        }
        .distinctUntilChanged()

    override suspend fun setKeepScreenOn(value: Boolean) {
        context.datastore.edit { preferences ->
            preferences[KEEP_SCREEN_ON] = value
        }
    }

    override suspend fun setTTSSpeed(value: Float) {
        context.datastore.edit { preferences ->
            preferences[TTS_SPEED] = value
        }
    }

    override suspend fun setTTSPitch(value: Float) {
        context.datastore.edit { preferences ->
            preferences[TTS_PITCH] = value
        }
    }

    override suspend fun setTTSLocale(value: String) {
        context.datastore.edit { preferences ->
            preferences[TTS_LOCALE] = value
        }
    }

    override suspend fun setTTSVoice(value: String) {
        context.datastore.edit { preferences ->
            preferences[TTS_VOICE] = value
        }
    }

    override suspend fun setAutoScrollSpeed(value: Int) {
        context.datastore.edit { preferences ->
            preferences[AUTO_SCROLL_SPEED] = value
        }
    }

    override suspend fun setDelayTimeAtStart(value: Int) {
        context.datastore.edit { preferences ->
            preferences[DELAY_TIME_AT_START] = value
        }
    }

    override suspend fun setDelayTimeAtEnd(value: Int) {
        context.datastore.edit { preferences ->
            preferences[DELAY_TIME_AT_END] = value
        }
    }

    override suspend fun setAutoScrollResumeMode(value: Boolean) {
        context.datastore.edit { preferences ->
            preferences[AUTO_SCROLL_RESUME_MODE] = value
        }
    }

    override suspend fun setAutoScrollResumeDelayTime(value: Int) {
        context.datastore.edit { preferences ->
            preferences[AUTO_SCROLL_RESUME_DELAY_TIME] = value
        }
    }

    override suspend fun setBackgroundColor(value: Int) {
        context.datastore.edit { preferences ->
            preferences[BACKGROUND_COLOR] = value
        }
    }

    override suspend fun setTextColor(value: Int) {
        context.datastore.edit { preferences ->
            preferences[TEXT_COLOR] = value
        }
    }

    override suspend fun setSelectedColorSet(value: Int) {
        context.datastore.edit { preferences ->
            preferences[SELECTED_COLOR_SET] = value
        }
    }

    override suspend fun setFontSize(value: Int) {
        context.datastore.edit { preferences ->
            preferences[FONT_SIZE] = value
        }
    }

    override suspend fun setTextAlign(value: Boolean) {
        context.datastore.edit { preferences ->
            preferences[TEXT_ALIGN] = value
        }
    }

    override suspend fun setTextIndent(value: Boolean) {
        context.datastore.edit { preferences ->
            preferences[TEXT_INDENT] = value
        }
    }

    override suspend fun setLineSpacing(value: Int) {
        context.datastore.edit { preferences ->
            preferences[LINE_SPACING] = value
        }
    }

    override suspend fun setFontFamily(value: Int) {
        context.datastore.edit { preferences ->
            preferences[FONT_FAMILY] = value
        }
    }

    override suspend fun setSortByFavorite(value: Boolean) {
        context.datastore.edit { preferences ->
            preferences[IS_SORTED_BY_FAVORITE] = value
        }
    }

    override suspend fun setEnableBackgroundMusic(value: Boolean) {
        context.datastore.edit { preferences ->
            preferences[ENABLE_BACKGROUND_MUSIC] = value
        }
    }

    override suspend fun setEnableOnlyRunWithTTS(value: Boolean) {
        context.datastore.edit { preferences ->
            preferences[ONLY_RUN_WITH_TTS] = value
        }
    }

    override suspend fun setPlayerVolume(value: Float) {
        context.datastore.edit { preferences ->
            preferences[PLAYER_VOLUME] = value
        }
    }

    override suspend fun setBookListView(value: Int) {
        context.datastore.edit { preferences ->
            preferences[BOOK_LIST_VIEW_TYPE] = value
        }
    }

    override suspend fun setImagePaddingState(value: Boolean) {
        context.datastore.edit { preferences ->
            preferences[IMAGE_PADDING_STATE] = value
        }
    }

    override suspend fun setBookmarkStyle(bookmarkStyle: BookmarkStyle) {
        context.datastore.edit { preferences ->
            preferences[BOOKMARK_STYLE] = bookmarkStyle.name
        }
    }
}