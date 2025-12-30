package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.MusicPathRepository

class MusicUseCase (
    private val musicRepository: MusicPathRepository
) {
    fun getSelectedMusicPaths() = musicRepository.getSelectedMusicPathsFlow()
}