package com.inkspire.ebookreader.domain.usecase

import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.repository.BookRepository

class SettingUseCase(
    private val bookRepository: BookRepository
) {
    suspend fun insertCategory(category: Category) = bookRepository.insertCategory(category)
    suspend fun deleteCategory(categories: List<Category>) = bookRepository.deleteCategory(categories)
    fun getBookCategoryFlow() = bookRepository.getBookCategory()
}