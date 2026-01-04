package com.inkspire.ebookreader.ui.home.libary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.BookWithCategoriesModel
import com.inkspire.ebookreader.domain.model.Category
import com.inkspire.ebookreader.domain.model.LibrarySettingPreferences
import com.inkspire.ebookreader.domain.usecase.LibraryDatastoreUseCase
import com.inkspire.ebookreader.domain.usecase.LibraryUseCase
import com.inkspire.ebookreader.util.BitmapUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class LibraryViewModel(
    private val libraryUseCase: LibraryUseCase,
    private val libraryDatastoreUseCase: LibraryDatastoreUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        _state.value
    )

    private val _rawBookList = MutableStateFlow<UiState<List<BookWithCategoriesModel>>>(UiState.None)

    init {
        viewModelScope.launch {
            libraryDatastoreUseCase.librarySettings.collectLatest { settings ->
                _state.update { it.copy(librarySettings = settings) }
            }
        }

        viewModelScope.launch {
            libraryUseCase.getBookCategory().collectLatest { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }

        viewModelScope.launch {
            libraryUseCase.getAllBooksWithCategories()
                .map { if (it.isEmpty()) UiState.Empty else UiState.Success { it } }
                .onStart { emit(UiState.Loading) }
                .catch { emit(UiState.Error(it)) }
                .collectLatest { _rawBookList.value = it }
        }

        viewModelScope.launch {
            combine(
                _rawBookList,
                _state.map { it.searchQuery }.distinctUntilChanged(),
                _state.map { it.categories }.distinctUntilChanged(),
                _state.map { it.librarySettings }.distinctUntilChanged()
            ) { rawBooksState, query, categories, settings ->
                processBookList(rawBooksState, query, categories, settings)
            }
            .flowOn(Dispatchers.Default)
            .collectLatest { filteredState ->
                _state.update { it.copy(bookList = filteredState) }
            }
        }
    }

    private fun processBookList(
        rawState: UiState<List<BookWithCategoriesModel>>,
        query: String,
        categories: List<Category>,
        settings: LibrarySettingPreferences
    ): UiState<List<BookWithCategoriesModel>> {
        return when (rawState) {
            is UiState.Success -> {
                val selectedCategoryNames = categories
                    .filter { it.isSelected }
                    .map { it.name }
                    .toSet()

                val filtered = rawState.data().filter { book ->
                    val matchesSearch = query.isBlank()
                            || book.title.contains(query.trim(), ignoreCase = true)
                            || book.authors.joinToString(",").contains(query.trim(), ignoreCase = true)

                    val matchesCategory = selectedCategoryNames.isEmpty()
                            || book.categories.any { it.name in selectedCategoryNames }

                    matchesSearch && matchesCategory
                }

                val sorted = if (settings.isSortedByFavorite) {
                    filtered.sortedByDescending { it.isFavorite }
                } else {
                    filtered
                }

                if (sorted.isEmpty() && query.isNotEmpty()) {
                    UiState.Success { emptyList() }
                } else if (sorted.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success { sorted }
                }
            }
            else -> rawState
        }
    }

    fun onAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            is LibraryAction.AddSelectedBook -> {
                _state.update { it.copy(selectedBookList = it.selectedBookList + action.book) }
            }
            is LibraryAction.ChangeChipState -> {
                // Determine new categories state
                val newCategories = _state.value.categories.map { chip ->
                    if (chip.id == action.categoryChip.id) chip.copy(isSelected = !chip.isSelected) else chip
                }
                // We update state immediately for UI responsiveness.
                // The 'combine' block above will trigger automatically because it observes this state.
                _state.update { it.copy(categories = newCategories) }
            }
            is LibraryAction.ResetChipState -> {
                val newCategories = _state.value.categories.map { it.copy(isSelected = false) }
                _state.update { it.copy(categories = newCategories) }
            }
            is LibraryAction.ConfirmDeleteBooks -> {
                viewModelScope.launch {
                    val booksToDelete = _state.value.selectedBookList
                    libraryUseCase.deleteBooks(booksToDelete)
                    yield() // Allow DB to process
                    processDeleteImages(booksToDelete.map { it.id })
                    _state.update { it.copy(selectedBookList = emptyList(), isOnDeletingBooks = false) }
                }
            }
            is LibraryAction.DeleteSelectedBooks -> {
                viewModelScope.launch {
                    libraryUseCase.deleteBooks(listOf(action.book))
                    yield()
                    processDeleteImages(listOf(action.book.id))
                }
            }
            is LibraryAction.RemoveSelectedBook -> {
                _state.update { it.copy(selectedBookList = it.selectedBookList - action.book) }
            }
            is LibraryAction.UpdateBookFavoriteState -> {
                viewModelScope.launch {
                    libraryUseCase.setBookAsFavorite(action.book.id, !action.book.isFavorite)
                }
            }
            is LibraryAction.UpdateBookListType -> {
                viewModelScope.launch {
                    val currentType = _state.value.librarySettings.bookListViewType
                    libraryDatastoreUseCase.setBookListViewType(if (currentType == 0) 1 else 0)
                }
            }
            is LibraryAction.UpdateDeletingState -> {
                _state.update { it.copy(isOnDeletingBooks = !_state.value.isOnDeletingBooks) }
            }
            is LibraryAction.UpdateSortState -> {
                viewModelScope.launch {
                    val currentSort = _state.value.librarySettings.isSortedByFavorite
                    libraryDatastoreUseCase.setSortByFavorite(!currentSort)
                }
            }
            is LibraryAction.ChangeBottomSheetVisibility -> {
                _state.update { it.copy(bottomSheetVisibility = !_state.value.bottomSheetVisibility) }
            }
            is LibraryAction.ChangeFabVisibility -> {
                _state.update { it.copy(fabVisibility = action.visibility) }
            }
            is LibraryAction.ChangeFabExpandState -> {
                _state.update { it.copy(fabExpanded = action.visibility) }
            }
            is LibraryAction.OnOpenBook -> {
                viewModelScope.launch {
                    libraryUseCase.updateRecentRead(action.bookId)
                }
            }
        }
    }

    private fun processDeleteImages(bookIds: List<String>) {
        viewModelScope.launch {
            val imagePaths = libraryUseCase.getImagePathsByBookIds(bookIds)
            for (imagePathEntity in imagePaths) {
                BitmapUtil.deleteImageFromPrivateStorage(imagePathEntity.imagePath)
            }
            libraryUseCase.deleteByBookIds(bookIds)
        }
    }
}