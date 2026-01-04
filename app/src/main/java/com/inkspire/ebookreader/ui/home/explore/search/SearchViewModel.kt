package com.inkspire.ebookreader.ui.home.explore.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.ConnectivityObserver
import com.inkspire.ebookreader.common.TruyenFullScraper
import com.inkspire.ebookreader.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    connectivityObserver: ConnectivityObserver
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    private val baseUrl = "https://truyenfull.vision"

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.ChangeSelectedWebsite -> {
                _state.update {
                    it.copy(
                        selectedWebsite = action.selectedWebsite,
                        searchResult = UiState.None,
                        currentPage = 1,
                        maxPage = 1,
                        currentQuery = null,
                        currentCategory = null
                    )
                }
            }
            is SearchAction.PerformSearchQuery -> {
                if (action.query.isBlank()) return
                searchByQuery(action.query, 1)
            }
            is SearchAction.PerformSearchCategory -> {
                searchByCategory(action.category, 1)
            }
            is SearchAction.NextPage -> {
                val s = _state.value
                if (s.currentPage < s.maxPage) {
                    loadPage(s.currentPage + 1)
                }
            }
            is SearchAction.PreviousPage -> {
                val s = _state.value
                if (s.currentPage > 1) {
                    loadPage(s.currentPage - 1)
                }
            }
            is SearchAction.ClearResult -> {
                _state.update {
                    it.copy(
                        searchResult = UiState.None,
                        currentPage = 1,
                        maxPage = 1,
                        currentQuery = null,
                        currentCategory = null
                    )
                }
            }

            else -> {}
        }
    }

    private fun loadPage(page: Int) {
        val s = _state.value
        if (s.currentQuery != null) {
            searchByQuery(s.currentQuery, page)
        } else if (s.currentCategory != null) {
            searchByCategory(s.currentCategory, page)
        }
    }

    private fun searchByQuery(query: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(searchResult = UiState.Loading) }
            try {
                val result = TruyenFullScraper.search(baseUrl, query, page)

                if (result.data.isEmpty()) {
                    _state.update { it.copy(searchResult = UiState.Empty) }
                } else {
                    _state.update {
                        it.copy(
                            searchResult = UiState.Success { result.data },
                            currentPage = page,
                            maxPage = result.totalPages,
                            currentQuery = query,
                            currentCategory = null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(searchResult = UiState.Error(Throwable("Failed: ${e.message}"))) }
            }
        }
    }

    private fun searchByCategory(categorySlug: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(searchResult = UiState.Loading) }
            try {
                val result = TruyenFullScraper.fetchCategoryBooks(baseUrl, categorySlug, page)

                if (result.data.isEmpty()) {
                    _state.update { it.copy(searchResult = UiState.Empty) }
                } else {
                    _state.update {
                        it.copy(
                            searchResult = UiState.Success { result.data },
                            currentPage = page,
                            maxPage = result.totalPages,
                            currentCategory = categorySlug,
                            currentQuery = null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(searchResult = UiState.Error(Throwable("Failed: ${e.message}"))) }
            }
        }
    }
}