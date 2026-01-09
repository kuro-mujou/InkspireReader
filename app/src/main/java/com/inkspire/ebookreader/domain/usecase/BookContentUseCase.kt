package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.domain.model.HighlightToInsert
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ChapterRepository
import com.inkspire.ebookreader.domain.repository.HighlightRepository
import com.inkspire.ebookreader.domain.repository.TableOfContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class BookContentUseCase(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository,
    private val tableOfContentRepository: TableOfContentRepository,
    private val highlightRepository: HighlightRepository
) {
    fun getBookAsFlow(bookId: String) = bookRepository.getBookAsFlow(bookId)
    fun getTableOfContentAsFlow(bookId: String) = tableOfContentRepository.getTableOfContentAsFlow(bookId)
    fun getChapterContentFlow(bookId: String, tocId: Int): Flow<Chapter?> {
        val contentFlow = chapterRepository.getChapterContentFlow(bookId, tocId)
        val highlightFlow = highlightRepository.getHighlightsForChapterFlow(bookId, tocId)

        return combine(contentFlow, highlightFlow) { entity, highlightEntities ->
            if (entity == null) return@combine null
            val highlightMap = highlightEntities.groupBy { it.paragraphIndex }
            Chapter(
                bookId = entity.bookId,
                tocId = entity.tocId,
                chapterTitle = entity.chapterTitle,
                content = entity.content,
                highlights = highlightMap,
            )
        }
    }

    suspend fun saveBookInfoChapterIndex(bookId: String, chapterIndex: Int) = bookRepository.saveBookInfoChapterIndex(bookId, chapterIndex)
    suspend fun saveBookInfoParagraphIndex(bookId: String, paragraphIndex: Int) = bookRepository.saveBookInfoParagraphIndex(bookId, paragraphIndex)
    suspend fun addHighlight(highlight: HighlightToInsert) = highlightRepository.insertHighlight(highlight)
}