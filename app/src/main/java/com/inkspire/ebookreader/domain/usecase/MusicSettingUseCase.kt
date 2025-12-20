package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.model.MusicItem
import com.inkspire.ebookreader.domain.repository.MusicPathRepository

class MusicSettingUseCase(
    private val musicPathRepository: MusicPathRepository,
) {
    fun getMusicPaths() = musicPathRepository.getMusicPaths()
    suspend fun saveMusicPaths(musicPathEntity: List<MusicItem>) = musicPathRepository.saveMusicPaths(musicPathEntity)
    suspend fun setMusicAsFavorite(id: Int, isFavorite: Boolean) = musicPathRepository.setMusicAsFavorite(id, isFavorite)
    suspend fun setMusicAsSelected(id: Int, isSelected: Boolean) = musicPathRepository.setMusicAsSelected(id, isSelected)
    suspend fun deleteMusicPath(id: Int) = musicPathRepository.deleteMusicPath(id)
}