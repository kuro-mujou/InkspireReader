package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ChapterRepository

class TTSContentUseCase(
    private val chapterRepository: ChapterRepository,
    private val bookRepository: BookRepository
) {
    suspend fun getChapterContent(bookId: String, chapterIndex: Int) = chapterRepository.getChapterContent(bookId, chapterIndex)
    suspend fun saveBookInfoChapterIndex(bookId: String, chapterIndex: Int) = bookRepository.saveBookInfoChapterIndex(bookId, chapterIndex)
    suspend fun saveBookInfoParagraphIndex(bookId: String, paragraphIndex: Int) = bookRepository.saveBookInfoParagraphIndex(bookId, paragraphIndex)
}