package com.inkspire.ebookreader.ui.home.recentbook

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecentBookViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {
    companion object {
        private const val TAG = "RecentBookViewModel"
    }
    private val _state = MutableStateFlow(RecentBookState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    init {
        viewModelScope.launch {
            bookRepository.getBookListForMainScreen()
                .map { books ->
                    if (books.isEmpty()) {
                        _state.update { it.copy(recentBookState = UiState.Empty) }
                    } else {
                        _state.update { it.copy(recentBookState = UiState.Success(books)) }
                    }
                }
                .onStart {
                    _state.update { it.copy(recentBookState = UiState.Loading) }
                }
                .catch { exception ->
                    Log.e(TAG, "Error: $exception")
                    _state.update { it.copy(recentBookState = UiState.Error(exception)) }
                }
                .launchIn(viewModelScope)
        }
    }
}


//    private val logger = LoggerFactory.getLogger(RecentBookViewModel::class.java)
//
//    init {
////        tryFetchTruyenFull()
//        tryFetchTangThuVien()
//    }
//
//    private fun tryFetchTruyenFull() {
//        val inputUrl = "https://truyenfull.vision/thinh-triet-duong-co-quy/"
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val bookInfo = TruyenFullScraper.fetchBookDetails(inputUrl)
//                val json = Json { prettyPrint = true }
//                logger.info(json.encodeToString(bookInfo))
//
//                val tocList = TruyenFullScraper.fetchTOC(bookInfo.internalId, inputUrl)
//
//                tocList.forEach { chapterRef ->
//                    logger.info("Fetching: ${json.encodeToString(chapterRef)}")
//                    val htmlContent = TruyenFullScraper.fetchChapterContent(chapterRef.url)
//                    logger.info(htmlContent)
//                    delay(150)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun tryFetchTangThuVien() {
//        val inputUrl = "https://tangthuvien.net/doc-truyen/kiem-lai---tang-thu-vien"
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val bookInfo = TangThuVienScraper.fetchBookDetails(inputUrl)
//                val tocList = TangThuVienScraper.fetchTOC(bookInfo.internalId, inputUrl)
//
//                tocList.forEach { chapterRef ->
//                    val content = TangThuVienScraper.fetchChapterContent(chapterRef.url)
//
//                    logger.info("Content Length: ${content.length} characters")
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }