package com.inkspire.ebookreader.domain.repository

import com.inkspire.ebookreader.data.model.BookWithCategories
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getBookCategoryFlow(): Flow<List<Category>>
    fun getFlowBookWithCategories(bookId: String): Flow<BookWithCategories>
    fun getBooksMatchingAnySelectedCategory(selectedCategoryIds: List<Int>): Flow<List<Book>>
    suspend fun deleteCategory(categories: List<Category>)
    suspend fun insertCategory(category: Category): Long
    suspend fun addCategoryToBook(bookId: String, category: Category)
    suspend fun updateBookCategory(bookId: String,categories: List<Category>)
}