package com.inkspire.ebookreader.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.TableOfContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookId: String,
    private val bookRepository: BookRepository,
    private val tableOfContentRepository: TableOfContentRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    init {
        viewModelScope.launch {
            tableOfContentRepository
                .getFlowTableOfContents(bookId)
                .collectLatest { tableOfContents ->
                    _state.update {
                        it.copy(
                            tableOfContents = tableOfContents
                        )
                    }
                }
        }
        viewModelScope.launch {
            bookRepository.getFlowBookWithCategories(bookId).collectLatest { book ->
                _state.update { it.copy( bookWithCategories = book )}
            }
        }
        viewModelScope.launch {
            getSelectableCategoriesForBook(bookId).collectLatest { categories ->
                _state.update { it.copy( categories = categories )}
            }
        }
    }

    fun onAction(action: BookDetailAction) {
        when (action) {
            is BookDetailAction.OnDrawerItemClick -> {
                viewModelScope.launch {
                    bookRepository.saveBookInfoChapterIndex(bookId, action.index)
                    bookRepository.saveBookInfoParagraphIndex(bookId, 0)
                }
            }

            is BookDetailAction.OnBookMarkClick -> {
                viewModelScope.launch {
                    _state.value.bookWithCategories?.book?.isFavorite?.let { bookRepository.setBookAsFavorite(bookId, !it) }
                }
            }

            is BookDetailAction.ChangeChipState ->{
                _state.update {
                    it.copy(
                        categories = it.categories.map { chip ->
                            if (chip.id == action.category.id) {
                                chip.copy(isSelected = !chip.isSelected)
                            } else {
                                chip
                            }
                        }
                    )
                }
                viewModelScope.launch {
                    bookRepository.updateBookCategory(
                        bookId = bookId,
                        categories = _state.value.categories
                    )
                }
            }

            BookDetailAction.ChangeCategoryMenuVisibility -> {
                _state.update { it.copy(isShowCategoryMenu = !it.isShowCategoryMenu) }
            }
        }
    }

    fun getSelectableCategoriesForBook(bookId: String): Flow<List<Category>> {
        val bookWithCategoriesFlow = bookRepository.getFlowBookWithCategories(bookId)
        val allCategoriesFlow = bookRepository.getBookCategory()

        return combine(bookWithCategoriesFlow, allCategoriesFlow) { bookWithCategories, allCategories ->
            val bookCategoryIds = bookWithCategories.categories.map { it.categoryId }.toSet()

            allCategories.map { categoryEntity ->
                Category(
                    id = categoryEntity.id,
                    name = categoryEntity.name,
                    color = categoryEntity.color,
                    isSelected = categoryEntity.id in bookCategoryIds
                )
            }
        }
    }
}