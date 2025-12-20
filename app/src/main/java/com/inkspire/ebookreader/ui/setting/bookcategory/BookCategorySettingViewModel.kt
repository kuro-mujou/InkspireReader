package com.inkspire.ebookreader.ui.setting.bookcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.usecase.BookCategorySettingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookCategorySettingViewModel(
    private val bookCategorySettingUseCase: BookCategorySettingUseCase
): ViewModel() {
    private val _state = MutableStateFlow(BookCategorySettingState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    init {
        viewModelScope.launch {
            bookCategorySettingUseCase.getBookCategoryFlow().collectLatest { categories ->
                _state.update { it.copy(bookCategories = categories) }
            }
        }
    }

    fun onAction(action: BookCategorySettingAction) {
        when (action) {
            is BookCategorySettingAction.AddCategory -> {
                viewModelScope.launch {
                    bookCategorySettingUseCase.insertCategory(action.category)
                }
            }

            is BookCategorySettingAction.ChangeChipState -> {
                _state.update {
                    it.copy(
                        bookCategories = it.bookCategories.map { chip ->
                            if (chip.id == action.chip.id) {
                                chip.copy(isSelected = !chip.isSelected)
                            } else {
                                chip
                            }
                        }
                    )
                }
            }

            is BookCategorySettingAction.DeleteCategory -> {
                viewModelScope.launch {
                    bookCategorySettingUseCase.deleteCategory(
                        _state.value.bookCategories.filter { it.isSelected }
                    )
                }
            }

            is BookCategorySettingAction.ResetChipState -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            bookCategories = it.bookCategories.map { category ->
                                category.copy(isSelected = false)
                            }
                        )
                    }
                }
            }
        }
    }
}