package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.data.database.model.HighlightEntity
import com.inkspire.ebookreader.domain.model.Highlight
import com.inkspire.ebookreader.domain.model.HighlightToInsert
import kotlinx.coroutines.flow.Flow

interface HighlightRepository {
    fun getHighlightsForChapterFlow(bookId: String, tocId: Int): Flow<List<Highlight>>
    suspend fun getHighlightsForParagraph(bookId: String, tocId: Int, paragraphIndex: Int): List<Highlight>
    suspend fun insertHighlight(highlight: HighlightToInsert)
    suspend fun replaceHighlightsForParagraph(
        bookId: String,
        tocId: Int,
        paragraphIndex: Int,
        newHighlights: List<HighlightToInsert>
    )
    suspend fun getHighlightsForChapterSync(bookId: String, tocId: Int): List<HighlightEntity>
    suspend fun getAllHighlightsForBook(bookId: String): List<HighlightEntity>
}