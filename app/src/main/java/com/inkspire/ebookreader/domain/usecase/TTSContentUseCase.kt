package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.ChapterRepository

class TTSContentUseCase(
    private val chapterRepository: ChapterRepository,
) {
    suspend fun getChapterContent(bookId: String, chapterIndex: Int) = chapterRepository.getChapterContent(bookId, chapterIndex)
}