package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.data.database.model.TableOfContentEntity
import com.inkspire.ebookreader.domain.model.TableOfContent
import kotlinx.coroutines.flow.Flow

interface TableOfContentRepository {
    suspend fun saveTableOfContent(tocEntity: TableOfContentEntity): Long

    fun getTableOfContentAsFlow(bookId: String): Flow<List<TableOfContent>>
    suspend fun updateTableOfContentFavoriteStatus(bookId: String, index: Int, isFavorite: Boolean)

    suspend fun getTableOfContents(bookId: String): List<TableOfContent>
    suspend fun getTableOfContent(bookId: String, tocId: Int): TableOfContent?
    suspend fun addChapter(chapter: TableOfContent)
    suspend fun updateTableOfContentTitle(bookId: String, index: Int, chapterTitle: String)
    suspend fun deleteTableOfContent(bookId: String, tocId: Int)
    suspend fun updateTableOfContentIndexOnDelete(bookId: String, index: Int)
    suspend fun updateTableOfContentIndexOnInsert(bookId: String, index: Int)
    suspend fun swapTableOfContent(currentBookId: String, draggedItemTocId: Int, fromIndex: Int, toIndex: Int)
}