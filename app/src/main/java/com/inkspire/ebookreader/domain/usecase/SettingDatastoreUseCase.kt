package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class SettingDatastoreUseCase(
    private val datastoreRepository: DatastoreRepository
) {
    fun getKeepScreenOn() = datastoreRepository.getKeepScreenOn()
    fun getEnableSpecialArt() = datastoreRepository.getEnableSpecialArt()
    fun getTtsLocale() = datastoreRepository.getTtsLocale()
    fun getTtsSpeed() = datastoreRepository.getTtsSpeed()
    fun getTtsPitch() = datastoreRepository.getTtsPitch()
    fun getTtsVoice() = datastoreRepository.getTtsVoice()
    fun getUnlockSpecialCodeStatus() = datastoreRepository.getUnlockSpecialCodeStatus()
    fun getAutoScrollSpeed() = datastoreRepository.getAutoScrollSpeed()
    fun getDelayTimeAtStart() = datastoreRepository.getDelayTimeAtStart()
    fun getDelayTimeAtEnd() = datastoreRepository.getDelayTimeAtEnd()
    fun getAutoScrollResumeDelayTime() = datastoreRepository.getAutoScrollResumeDelayTime()
    fun getAutoScrollResumeMode() = datastoreRepository.getAutoScrollResumeMode()
    fun getBookmarkStyle() = datastoreRepository.getBookmarkStyle()
    fun getEnableBackgroundMusic() = datastoreRepository.getEnableBackgroundMusic()

    suspend fun setEnableSpecialArt(enable: Boolean) = datastoreRepository.setEnableSpecialArt(enable)
    suspend fun setUnlockSpecialCodeStatus(status: Boolean) = datastoreRepository.setUnlockSpecialCodeStatus(status)
    suspend fun setTTSLocale(value: String) = datastoreRepository.setTTSLocale(value)
    suspend fun setTTSVoice(value: String) = datastoreRepository.setTTSVoice(value)
    suspend fun setTTSSpeed(value: Float) = datastoreRepository.setTTSSpeed(value)
    suspend fun setTTSPitch(value: Float) = datastoreRepository.setTTSPitch(value)
    suspend fun setAutoScrollSpeed(value: Int) = datastoreRepository.setAutoScrollSpeed(value)
    suspend fun setAutoScrollResumeDelayTime(value: Int) = datastoreRepository.setAutoScrollResumeDelayTime(value)
    suspend fun setDelayTimeAtStart(value: Int) = datastoreRepository.setDelayTimeAtStart(value)
    suspend fun setDelayTimeAtEnd(value: Int) = datastoreRepository.setDelayTimeAtEnd(value)
    suspend fun setAutoScrollResumeMode(value: Boolean) = datastoreRepository.setAutoScrollResumeMode(value)
    suspend fun setPlayerVolume(value: Float) = datastoreRepository.setPlayerVolume(value)
    suspend fun setEnableBackgroundMusic(value: Boolean) = datastoreRepository.setEnableBackgroundMusic(value)
    suspend fun setBookmarkStyle(value: BookmarkStyle) = datastoreRepository.setBookmarkStyle(value)
    suspend fun setKeepScreenOn(value: Boolean) = datastoreRepository.setKeepScreenOn(value)
}