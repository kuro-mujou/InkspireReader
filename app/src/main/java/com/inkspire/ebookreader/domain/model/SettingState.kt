package com.inkspire.ebookreader.domain.model

import android.speech.tts.Voice
import com.inkspire.ebookreader.common.BookmarkStyle
import java.util.Locale

data class SettingState(
    val temp: Int = 0,
    val keepScreenOn: Boolean = false,
    val isAutoResumeScrollMode: Boolean = false,
    val openCategoryMenu: Boolean = false,
    val specialCodeDialog: Boolean = false,
    val openBackgroundMusicMenu: Boolean = false,
    val openBookmarkThemeMenu: Boolean = false,
    val openAutoScrollMenu: Boolean = false,
    val openTTSVoiceMenu: Boolean = false,
    val openSpecialCodeDialog: Boolean = false,
    val currentPitch: Float = 1f,
    val currentSpeed: Float = 1f,
    val currentLanguage: Locale? = null,
    val currentVoice: Voice? = null,
    val currentScrollSpeed: Int = 10000,
    val delayAtStart: Int = 3000,
    val delayAtEnd: Int = 3000,
    val delayResumeMode: Int = 1000,
    val enableBackgroundMusic : Boolean = false,
    val selectedBookmarkStyle: BookmarkStyle = BookmarkStyle.WAVE_WITH_BIRDS,
    val bookCategories: List<Category> = emptyList(),
    val unlockSpecialCodeStatus: Boolean = false,
    val enableSpecialArt: Boolean = false,
)