package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.TableOfContentRepository

class TableOfContentUseCase(
    private val tableOfContentRepository: TableOfContentRepository
) {
    suspend fun updateChapterFavoriteState(bookId: String, chapterIndex: Int, isFavorite: Boolean) =
        tableOfContentRepository.updateTableOfContentFavoriteStatus(bookId, chapterIndex, isFavorite)
}