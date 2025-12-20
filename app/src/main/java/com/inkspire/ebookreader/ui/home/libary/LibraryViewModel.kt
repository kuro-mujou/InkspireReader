package com.inkspire.ebookreader.ui.home.libary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.usecase.LibraryDatastoreUseCase
import com.inkspire.ebookreader.domain.usecase.LibraryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File

class LibraryViewModel(
    private val libraryUseCase : LibraryUseCase,
    private val libraryDatastoreUseCase: LibraryDatastoreUseCase
): ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    init {
        viewModelScope.launch {
            libraryDatastoreUseCase.getBookListViewType().collectLatest { result ->
                _state.update { it.copy(listViewType = result) }
            }
        }
        viewModelScope.launch {
            libraryDatastoreUseCase.getIsSortedByFavorite().collectLatest { result ->
                _state.update { it.copy(isSortedByFavorite = result) }
            }
        }
        viewModelScope.launch {
            libraryUseCase.getBookCategory().collectLatest { categories->
                _state.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            libraryUseCase.getAllBooksFlow()
                .map { bookList ->
                    if (bookList.isEmpty()) {
                        _state.update { it.copy(bookList = UiState.Empty) }
                    } else {
                        _state.update { it.copy(bookList = UiState.Success(bookList)) }
                    }
                }
                .onStart {
                    _state.update { it.copy(bookList = UiState.Loading) }
                }
                .catch { exception ->
                    _state.update { it.copy(bookList = UiState.Error(exception)) }
                }
                .stateIn(viewModelScope)
        }
    }

    fun onAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.AddSelectedBook -> {
                _state.update { it.copy(selectedBookList = it.selectedBookList + action.book) }
            }
            is LibraryAction.ChangeChipState -> {
                _state.update {
                    it.copy(
                        categories = it.categories.map { chip ->
                            if (chip.id == action.categoryChip.id) {
                                chip.copy(isSelected = !chip.isSelected)
                            } else {
                                chip
                            }
                        }
                    )
                }
            }
            is LibraryAction.ConfirmDeleteBooks -> {
                viewModelScope.launch {
                    libraryUseCase.deleteBooks(_state.value.selectedBookList)
                    yield()
                    processDeleteImages(_state.value.selectedBookList.map { it.id })
                    _state.update { it.copy(selectedBookList = emptyList()) }
                }
            }
            is LibraryAction.DeleteSelectedBooks -> {
                viewModelScope.launch {
                    libraryUseCase.deleteBooks(listOf(action.book))
                    yield()
                    processDeleteImages(listOf(action.book).map { it.id })
                }
            }
            is LibraryAction.RemoveSelectedBook -> {
                _state.update { it.copy(selectedBookList = it.selectedBookList - action.book) }
            }
            is LibraryAction.ResetChipState -> {
                _state.update {
                    it.copy(
                        categories = it.categories.map { chip ->
                            chip.copy(isSelected = false)
                        }
                    )
                }
            }
            is LibraryAction.UpdateBookFavoriteState -> {
                viewModelScope.launch {
                    libraryUseCase.setBookAsFavorite(action.book.id, !action.book.isFavorite)
                }
            }
            is LibraryAction.UpdateBookListType -> {
                viewModelScope.launch {
                    if (_state.value.listViewType == 0) {
                        libraryDatastoreUseCase.setBookListViewType(1)
                    } else {
                        libraryDatastoreUseCase.setBookListViewType(0)
                    }
                }
            }
            is LibraryAction.UpdateDeletingState -> {
                _state.update { it.copy(isOnDeletingBooks = !_state.value.isOnDeletingBooks) }
            }
            is LibraryAction.UpdateSortState -> {
                viewModelScope.launch {
                    libraryDatastoreUseCase.setSortByFavorite(!_state.value.isSortedByFavorite)
                }
            }
            is LibraryAction.ChangeBottomSheetVisibility -> {
                _state.update { it.copy(bottomSheetVisibility = !_state.value.bottomSheetVisibility) }
            }
            is LibraryAction.ChangeDriveDialogVisibility -> {
                _state.update { it.copy(driveDialogVisibility = !_state.value.driveDialogVisibility) }
            }
            is LibraryAction.ChangeFabVisibility -> {
                _state.update { it.copy(fabVisibility = action.visibility) }
            }
            is LibraryAction.ChangeFabExpandState -> {
                _state.update { it.copy(fabExpanded = action.visibility) }
            }
        }
    }

    private fun processDeleteImages(bookIds: List<String>) {
        viewModelScope.launch {
            val imagePaths = libraryUseCase.getImagePathsByBookIds(bookIds)
            for (imagePathEntity in imagePaths) {
                val file = File(imagePathEntity.imagePath)
                if (file.exists()) {
                    file.delete()
                }
            }
            libraryUseCase.deleteByBookIds(bookIds)
        }
    }
}