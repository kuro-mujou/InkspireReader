package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.database.dao.HighlightDao
import com.inkspire.ebookreader.data.mapper.toDataClass
import com.inkspire.ebookreader.data.mapper.toEntity
import com.inkspire.ebookreader.domain.model.Highlight
import com.inkspire.ebookreader.domain.model.HighlightToInsert
import com.inkspire.ebookreader.domain.repository.HighlightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HighlightRepositoryImpl(
    private val highlightDao: HighlightDao
): HighlightRepository {
    override fun getHighlightsForChapterFlow(bookId: String, tocId: Int): Flow<List<Highlight>> {
        return highlightDao.getHighlightsForChapter(bookId, tocId).map { entities ->
            entities.map { it.toDataClass() }
        }
    }

    override suspend fun insertHighlight(highlight: HighlightToInsert) {
        highlightDao.insertHighlight(highlight.toEntity())
    }
}