package com.inkspire.ebookreader.data.repository

import com.inkspire.ebookreader.data.dao.CategoryDao
import com.inkspire.ebookreader.data.model.BookWithCategories
import com.inkspire.ebookreader.domain.mapper.toDataClass
import com.inkspire.ebookreader.domain.mapper.toEntity
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun addCategoryToBook(bookId: String, category: Category) {
        categoryDao.addCategoryToBook(bookId, category.toEntity().categoryId)
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(categories: List<Category>) {
        categories.forEach {
            categoryDao.deleteCategory(it.toEntity())
        }
    }

    override fun getBookCategoryFlow(): Flow<List<Category>> {
        return categoryDao.getBookCategory().map { categoryEntity ->
            categoryEntity.map { it.toDataClass() }
        }
    }

    override fun getFlowBookWithCategories(bookId: String): Flow<BookWithCategories> {
        return categoryDao.getFlowBookWithCategories(bookId)
    }

    override suspend fun updateBookCategory(bookId: String, categories: List<Category>) {
        val currentCategories = categoryDao.getBookWithCategories(bookId)?.categories.orEmpty()

        val currentCategoryIds = currentCategories.map { it.categoryId }.toSet()
        val selectedCategoryIds = categories.filter { it.isSelected }.map { it.id }.toSet()

        val categoriesToRemove = currentCategoryIds - selectedCategoryIds
        val categoriesToAdd = selectedCategoryIds - currentCategoryIds

        categoriesToRemove.forEach { categoryId ->
            categoryDao.deleteCategoryFromBook(bookId, categoryId!!)
        }

        categoriesToAdd.forEach { categoryId ->
            categoryDao.addCategoryToBook(bookId, categoryId!!)
        }
    }

    override fun getBooksMatchingAnySelectedCategory(selectedCategoryIds: List<Int>): Flow<List<Book>> {
        return categoryDao.getBooksByAnyCategory(selectedCategoryIds).map { bookEntities ->
            bookEntities.map { it.toDataClass() }
        }
    }
}