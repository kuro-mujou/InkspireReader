package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.TableOfContentRepository

class BookDetailUseCase(
    private val bookRepository: BookRepository,
    private val tableOfContentRepository: TableOfContentRepository
) {
    fun getFlowTableOfContents(bookId: String) = tableOfContentRepository.getTableOfContentAsFlow(bookId)
    fun getFlowBookWithCategories(bookId: String) = bookRepository.getFlowBookWithCategories(bookId)
    fun getBookCategoryFlow() = bookRepository.getBookCategory()
    suspend fun setBookAsFavorite(bookId: String, isFavorite: Boolean) = bookRepository.setBookAsFavorite(bookId, isFavorite)
    suspend fun updateBookCategory(bookId: String, categories: List<Category>) = bookRepository.updateBookCategory(bookId, categories)
    suspend fun saveBookInfoChapterIndex(bookId: String, chapterIndex: Int) = bookRepository.saveBookInfoChapterIndex(bookId, chapterIndex)
    suspend fun saveBookInfoParagraphIndex(bookId: String, paragraphIndex: Int) = bookRepository.saveBookInfoParagraphIndex(bookId, paragraphIndex)
}