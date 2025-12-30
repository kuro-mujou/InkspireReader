package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.DatastoreRepository

class TTSDatastoreUseCase (
    private val datastoreRepository: DatastoreRepository
){
    fun getTtsLocale() = datastoreRepository.getTtsLocale()
    fun getTtsSpeed() = datastoreRepository.getTtsSpeed()
    fun getTtsPitch() = datastoreRepository.getTtsPitch()
    fun getTtsVoice() = datastoreRepository.getTtsVoice()
    fun getEnableBackgroundMusic() = datastoreRepository.getEnableBackgroundMusic()
    fun getPlayerVolume() = datastoreRepository.getPlayerVolume()
}