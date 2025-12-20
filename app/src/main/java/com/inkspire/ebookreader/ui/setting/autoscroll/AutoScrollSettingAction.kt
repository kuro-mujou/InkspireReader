package com.inkspire.ebookreader.ui.setting.autoscroll

sealed interface AutoScrollSettingAction {
    data class UpdateScrollSpeed(val speed: Int) : AutoScrollSettingAction
    data class UpdateDelayAtStart(val delay: Int) : AutoScrollSettingAction
    data class UpdateDelayAtEnd(val delay: Int) : AutoScrollSettingAction
    data class UpdateAutoResumeScrollMode(val autoResume: Boolean) : AutoScrollSettingAction
    data class UpdateDelayResumeMode(val delay: Int) : AutoScrollSettingAction
}