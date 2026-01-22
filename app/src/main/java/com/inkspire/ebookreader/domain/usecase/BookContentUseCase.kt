package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.data.mapper.toInsertModel
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.domain.model.Highlight
import com.inkspire.ebookreader.domain.model.HighlightToInsert
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ChapterRepository
import com.inkspire.ebookreader.domain.repository.HiddenTextRepository
import com.inkspire.ebookreader.domain.repository.HighlightRepository
import com.inkspire.ebookreader.domain.repository.TableOfContentRepository
import com.inkspire.ebookreader.util.HighlightUtil
import com.inkspire.ebookreader.util.TextFilterTransformer
import com.inkspire.ebookreader.util.TextMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class BookContentUseCase(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository,
    private val tableOfContentRepository: TableOfContentRepository,
    private val highlightRepository: HighlightRepository,
    private val hiddenTextRepository: HiddenTextRepository
) {
    fun getBookAsFlow(bookId: String) = bookRepository.getBookAsFlow(bookId)
    fun getTableOfContentAsFlow(bookId: String) = tableOfContentRepository.getTableOfContentAsFlow(bookId)
    fun getChapterContentFlow(bookId: String, tocId: Int): Flow<Chapter?> {
        val contentFlow = chapterRepository.getChapterContentFlow(bookId, tocId)
        val highlightFlow = highlightRepository.getHighlightsForChapterFlow(bookId, tocId)
        val hiddenTextFlow = hiddenTextRepository.getHiddenTexts()

        return combine(contentFlow, highlightFlow, hiddenTextFlow) { entity, highlightEntities, hiddenEntities ->
            if (entity == null) return@combine null

            val highlightMap = highlightEntities.groupBy { it.paragraphIndex }

            val hiddenTexts = hiddenEntities.map { it.textToHide }

            val newContent = ArrayList<String>()
            val newHighlightMap = HashMap<Int, List<Highlight>>()

            entity.content.forEachIndexed { index, rawParagraph ->
                val highlightsForPara = highlightMap[index] ?: emptyList()
                val result = TextFilterTransformer.applyFilters(rawParagraph, highlightsForPara, hiddenTexts)

                newContent.add(result.displayText)
                if (result.adjustedHighlights.isNotEmpty()) {
                    newHighlightMap[index] = result.adjustedHighlights
                }
            }

            Chapter(
                bookId = entity.bookId,
                tocId = entity.tocId,
                chapterTitle = entity.chapterTitle,
                content = newContent,
                highlights = newHighlightMap,
            )
        }
    }

    suspend fun updateParagraphContent(
        bookId: String,
        tocId: Int,
        paragraphIndex: Int,
        visualStart: Int,
        visualEnd: Int,
        replacementText: String
    ) {
        val chapterContent = chapterRepository.getChapterContent(bookId, tocId) ?: return
        val rawParagraph = chapterContent.content.getOrNull(paragraphIndex) ?: return

        val mapped = TextMapper.convertToAnnotatedStringWithMap(rawParagraph)

        if (visualStart >= mapped.displayToRawMap.size || visualEnd > mapped.displayToRawMap.size) return

        val rawStart = mapped.displayToRawMap[visualStart]
        val rawEnd = mapped.displayToRawMap[visualEnd]

        val sb = StringBuilder(rawParagraph)
        sb.replace(rawStart, rawEnd, replacementText)
        val newRawParagraph = sb.toString()

        val newContentList = chapterContent.content.toMutableList()
        newContentList[paragraphIndex] = newRawParagraph

        val currentHighlights = highlightRepository.getHighlightsForParagraph(bookId, tocId, paragraphIndex)
        val newHighlights = HighlightUtil.recalculateHighlightsAfterEdit(
            currentHighlights,
            rawStart,
            rawEnd,
            replacementText.length
        ).map { it.toInsertModel().copy(bookId = bookId, tocId = tocId) }
        chapterRepository.updateChapterContent(bookId, tocId, newContentList)
        highlightRepository.replaceHighlightsForParagraph(bookId, tocId, paragraphIndex, newHighlights)
    }
    suspend fun saveBookInfoChapterIndex(bookId: String, chapterIndex: Int) = bookRepository.saveBookInfoChapterIndex(bookId, chapterIndex)
    suspend fun saveBookInfoParagraphIndex(bookId: String, paragraphIndex: Int) = bookRepository.saveBookInfoParagraphIndex(bookId, paragraphIndex)
    suspend fun replaceHighlightsForParagraph(bookId: String, tocId: Int, paragraphIndex: Int, newHighlights: List<HighlightToInsert>) = highlightRepository.replaceHighlightsForParagraph(bookId, tocId, paragraphIndex, newHighlights)
    suspend fun getHighlightsForParagraph(bookId: String, tocId: Int, paragraphIndex: Int) = highlightRepository.getHighlightsForParagraph(bookId, tocId, paragraphIndex)
    suspend fun addHiddenText(text: String) {
        hiddenTextRepository.addHiddenText(text)
    }
}