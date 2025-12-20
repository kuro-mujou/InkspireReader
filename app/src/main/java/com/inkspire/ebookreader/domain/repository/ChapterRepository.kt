package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.data.database.model.ChapterContentEntity
import com.inkspire.ebookreader.domain.model.Chapter
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {
    suspend fun getChapterContent(bookId: String, tocId: Int): Chapter?
    suspend fun saveChapterContent(chapter: ChapterContentEntity)
    suspend fun updateChapterContent(bookId: String, tocId: Int, content: List<String>)
    suspend fun deleteChapter(bookId: String, tocId: Int)
    suspend fun updateChapterIndexOnDelete(bookId: String, tocId: Int)
    suspend fun updateChapterIndexOnInsert(bookId: String, tocId: Int)
    suspend fun swapTocIndex(bookId: String, chapterContentId: Int, from: Int, to: Int)
    fun getChapterContentFlow(bookId: String, chapterIndex: Int): Flow<Chapter?>
}