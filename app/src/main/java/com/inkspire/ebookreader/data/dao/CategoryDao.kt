package com.inkspire.ebookreader.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.inkspire.ebookreader.data.model.BookCategoryCrossRef
import com.inkspire.ebookreader.data.model.BookEntity
import com.inkspire.ebookreader.data.model.BookWithCategories
import com.inkspire.ebookreader.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Transaction
    @Query("SELECT * FROM books WHERE bookId = :bookId")
    fun getFlowBookWithCategories(bookId: String): Flow<BookWithCategories>

    @Transaction
    @Query("SELECT * FROM books WHERE bookId = :bookId")
    suspend fun getBookWithCategories(bookId: String): BookWithCategories?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBookCategoryCrossRef(crossRef: BookCategoryCrossRef)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): CategoryEntity?

    @Transaction
    suspend fun addCategoryToBook(bookId: String, category: Int) {
        insertBookCategoryCrossRef(BookCategoryCrossRef(bookId, category))
    }

    @Query("DELETE FROM book_category_cross_ref WHERE bookId = :bookId")
    suspend fun deleteAllCategoryFromBook(bookId: String)

    @Query("DELETE FROM book_category_cross_ref WHERE bookId = :bookId AND categoryId = :categoryId")
    suspend fun deleteCategoryFromBook(bookId: String, categoryId: Int)

    @Transaction
    @Query("SELECT * FROM categories")
    fun getBookCategory(): Flow<List<CategoryEntity>>

    @Transaction
    @Query("""
        SELECT DISTINCT b.* FROM books b
        INNER JOIN book_category_cross_ref bc ON b.bookId = bc.bookId
        WHERE bc.categoryId IN (:categoryIds)
    """)
    fun getBooksByAnyCategory(categoryIds: List<Int>): Flow<List<BookEntity>>
}