package com.inkspire.ebookreader.ui.bookcontent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.Chapter
import com.inkspire.ebookreader.domain.repository.BookRepository
import com.inkspire.ebookreader.domain.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookContentViewModel(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository
): ViewModel() {
    private val bookId: String = "52fbd0f613ebbdcbac27278a1bd5a9bb"
    private val _uiState = MutableStateFlow(BookContentState())
    val uiState: StateFlow<BookContentState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _uiState.value
    )

    init {
        bookRepository.getBookAsFlow(bookId)
            .map { book ->
                _uiState.update { it.copy (bookState = UiState.Success(book)) }
            }
            .onStart {
                _uiState.update { it.copy(bookState = UiState.Loading) }
            }
            .catch { exception ->
                Log.e("ViewModel", "Error loading book", exception)
                _uiState.update { it.copy(bookState = UiState.Error(exception)) }
            }
            .distinctUntilChanged()
            .launchIn(viewModelScope)
    }

    fun updateRecentChapter(newChapter: Int) {
        viewModelScope.launch {
            bookRepository.saveBookInfoChapterIndex(bookId, newChapter)
        }
    }

    fun getChapterContent(chapterIndex: Int): Flow<UiState<Chapter>> {
        return chapterRepository.getChapterContentFlow(bookId, chapterIndex)
            .map { chapter -> UiState.Success(chapter) as UiState<Chapter>  }
            .onStart { emit(UiState.Loading) }
            .catch { emit(UiState.Error(it)) }
    }

    fun updateRecentParagraph(paragraphIndex: Int) {
        viewModelScope.launch {
            // You might need to update both recentChapter and recentParagraph
            // depending on your DB structure
            bookRepository.saveBookInfoParagraphIndex(bookId, paragraphIndex)
        }
    }
}