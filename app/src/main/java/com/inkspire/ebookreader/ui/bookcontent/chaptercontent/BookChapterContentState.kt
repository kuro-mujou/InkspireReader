package com.inkspire.ebookreader.ui.bookcontent.chaptercontent

import android.speech.tts.Voice
import com.inkspire.ebookreader.common.BookmarkStyle
import java.util.Locale

data class BookChapterContentState(
    val enableScaffoldBar: Boolean = true,

    val enablePagerScroll: Boolean = true,
    val enableUndoButton: Boolean = false,
    //content
    val currentChapterIndex: Int = -1,
    val firstVisibleItemIndex: Int = -1,
    val lastVisibleItemIndex: Int = -1,

    val previousChapterIndex: Int = 0,
    val flagTriggerScrolling: Boolean = false,
    val flagStartScrolling: Boolean = false,
    val flagScrollAdjusted: Boolean = false,
    val flagTriggerAdjustScroll: Boolean = false,
    val flagStartAdjustScroll: Boolean = false,
    val flagTriggerScrollForNote: Int = -1,

    val screenHeight: Int = 0,
    val screenWidth: Int = 0,
    //tts
    val currentSpeed: Float? = null,
    val currentPitch: Float? = null,
    val currentLanguage: Locale? = null,
    val currentVoice: Voice? = null,
    val scrollTime: Int = 0,
    val isSpeaking: Boolean = false,
    val isPaused: Boolean = false,
    val isFocused: Boolean = false,
    val currentReadingParagraph: Int = 0,
    //background music
    val enableBackgroundMusic: Boolean = false,

    val keepScreenOn: Boolean = false,
    val selectedBookmarkStyle: BookmarkStyle = BookmarkStyle.WAVE_WITH_BIRDS,
)