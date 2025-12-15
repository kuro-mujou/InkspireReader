package com.inkspire.ebookreader.ui.home.recentbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.common.TangThuVienScraper
import com.inkspire.ebookreader.common.TruyenFullScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class RecentBookViewModel : ViewModel() {
    private val logger = LoggerFactory.getLogger(RecentBookViewModel::class.java)

    init {
//        tryFetchTruyenFull()
        tryFetchTangThuVien()
    }

    private fun tryFetchTruyenFull() {
        val inputUrl = "https://truyenfull.vision/thinh-triet-duong-co-quy/"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bookInfo = TruyenFullScraper.fetchBookDetails(inputUrl)
                val json = Json { prettyPrint = true }
                logger.info(json.encodeToString(bookInfo))

                val tocList = TruyenFullScraper.fetchTOC(bookInfo.internalId, inputUrl)

                tocList.forEach { chapterRef ->
                    logger.info("Fetching: ${json.encodeToString(chapterRef)}")
                    val htmlContent = TruyenFullScraper.fetchChapterContent(chapterRef.url)
                    logger.info(htmlContent)
                    delay(150)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun tryFetchTangThuVien() {
        val inputUrl = "https://tangthuvien.net/doc-truyen/kiem-lai---tang-thu-vien"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bookInfo = TangThuVienScraper.fetchBookDetails(inputUrl)
                val tocList = TangThuVienScraper.fetchTOC(bookInfo.internalId, inputUrl)

                tocList.forEach { chapterRef ->
                    val content = TangThuVienScraper.fetchChapterContent(chapterRef.url)

                    logger.info("Content Length: ${content.length} characters")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}