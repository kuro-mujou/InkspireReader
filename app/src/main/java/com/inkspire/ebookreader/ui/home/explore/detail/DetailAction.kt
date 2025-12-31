package com.inkspire.ebookreader.ui.home.explore.detail

import com.inkspire.ebookreader.common.ScrapedBookInfo

sealed interface DetailAction {
    data object NavigateBack : DetailAction
    data class DownloadBook(val book: ScrapedBookInfo) : DetailAction
}