package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.dao.ChapterDao
import com.inkspire.ebookreader.data.model.ChapterContentEntity
import com.inkspire.ebookreader.domain.mapper.toDataClass
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.domain.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChapterRepositoryImpl(
    private val chapterDao: ChapterDao
) : ChapterRepository {
    override suspend fun getChapterContent(bookId: String, tocId: Int): Chapter? {
        return chapterDao
            .getChapterContent(bookId, tocId)
            ?.toDataClass()
    }

    override suspend fun saveChapterContent(chapter: ChapterContentEntity) {
        chapterDao.insertChapterContent(chapter)
    }

    override suspend fun updateChapterContent(
        bookId: String,
        tocId: Int,
        content: List<String>
    ) {
        chapterDao.updateChapterContent(bookId, tocId, content)
    }

    override suspend fun deleteChapter(bookId: String, tocId: Int) {
        chapterDao.deleteChapterContent(bookId, tocId)
    }

    override suspend fun updateChapterIndexOnDelete(bookId: String, tocId: Int) {
        chapterDao.updateChapterIndexOnDelete(bookId, tocId)
    }

    override suspend fun updateChapterIndexOnInsert(bookId: String, tocId: Int) {
        chapterDao.updateChapterIndexOnInsert(bookId, tocId)
    }

    override suspend fun swapTocIndex(bookId: String, chapterContentId: Int, from: Int, to: Int) {
        chapterDao.reorderChapterContent(
            bookId = bookId,
            chapterContentId = chapterContentId,
            startIndex = from,
            endIndex = to
        )
    }

    override fun getChapterContentFlow(bookId: String, chapterIndex: Int): Flow<Chapter?> {
        return chapterDao.getChapterContentFlow(bookId, chapterIndex)
            .map { it?.toDataClass() }
    }
}