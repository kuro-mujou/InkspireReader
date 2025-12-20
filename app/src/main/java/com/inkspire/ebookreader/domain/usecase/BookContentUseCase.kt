package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ChapterRepository

class BookContentUseCase(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository
) {
    fun getBookAsFlow(bookId: String) = bookRepository.getBookAsFlow(bookId)
    fun getChapterContentFlow(bookId: String, chapterIndex: Int) = chapterRepository.getChapterContentFlow(bookId, chapterIndex)
    suspend fun saveBookInfoChapterIndex(bookId: String, chapterIndex: Int) = bookRepository.saveBookInfoChapterIndex(bookId, chapterIndex)
    suspend fun saveBookInfoParagraphIndex(bookId: String, paragraphIndex: Int) = bookRepository.saveBookInfoParagraphIndex(bookId, paragraphIndex)
}