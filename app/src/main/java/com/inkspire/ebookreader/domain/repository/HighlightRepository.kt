package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.domain.model.Highlight
import com.inkspire.ebookreader.domain.model.HighlightToInsert
import kotlinx.coroutines.flow.Flow

interface HighlightRepository {
    fun getHighlightsForChapterFlow(bookId: String, tocId: Int): Flow<List<Highlight>>
    suspend fun insertHighlight(highlight: HighlightToInsert)
}