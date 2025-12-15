package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.data.model.ChapterContentEntity
import com.inkspire.ebookreader.domain.model.Chapter

interface ChapterRepository {
    suspend fun getChapterContent(bookId: String, tocId: Int): Chapter?
    suspend fun saveChapterContent(chapter: ChapterContentEntity)
    suspend fun updateChapterContent(bookId: String, tocId: Int, content: List<String>)
    suspend fun deleteChapter(bookId: String, tocId: Int)
    suspend fun updateChapterIndexOnDelete(bookId: String, tocId: Int)
    suspend fun updateChapterIndexOnInsert(bookId: String, tocId: Int)
    suspend fun swapTocIndex(bookId: String, chapterContentId: Int, from: Int, to: Int)
}