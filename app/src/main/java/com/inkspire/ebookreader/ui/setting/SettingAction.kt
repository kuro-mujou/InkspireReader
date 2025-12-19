package com.inkspire.ebookreader.ui.setting

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.domain.model.Category
import java.util.Locale

sealed interface SettingAction {
    data class OpenTTSVoiceMenu(val open: Boolean) : SettingAction
    data class KeepScreenOn(val keepScreenOn: Boolean) : SettingAction
    data class UpdateLanguage(val language: Locale?) : SettingAction
    data class UpdateVoice(val voice: Voice?) : SettingAction
    data class UpdateSpeed(val speed: Float) : SettingAction
    data class UpdatePitch(val pitch: Float) : SettingAction
    data class FixNullVoice(val tts: TextToSpeech) : SettingAction
    data class OpenAutoScrollMenu(val open: Boolean) : SettingAction
    data class UpdateScrollSpeed(val speed: Int) : SettingAction
    data class UpdateDelayAtStart(val delay: Int) : SettingAction
    data class UpdateDelayAtEnd(val delay: Int) : SettingAction
    data class UpdateAutoResumeScrollMode(val autoResume: Boolean) : SettingAction
    data class UpdateDelayResumeMode(val delay: Int) : SettingAction
    data class UpdateSelectedBookmarkStyle(val style: BookmarkStyle) : SettingAction
    data class OnEnableBackgroundMusicChange(val enable: Boolean) : SettingAction
    data class OnPlayerVolumeChange(val volume: Float) : SettingAction
    data class ChangeChipState(val chip: Category) : SettingAction
    data class AddCategory(val category: Category) : SettingAction
    data object DeleteCategory : SettingAction
    data object ResetChipState : SettingAction
    data object OpenSpecialCodeSuccess : SettingAction
    data class UpdateEnableSpecialArt(val enable: Boolean) : SettingAction
    data class OpenBackgroundMusicMenu(val open: Boolean) : SettingAction
    data class OpenBookmarkThemeMenu(val open: Boolean) : SettingAction
    data class OpenCategoryMenu(val open: Boolean) : SettingAction
    data class OpenSpecialCodeDialog(val open: Boolean) : SettingAction
}