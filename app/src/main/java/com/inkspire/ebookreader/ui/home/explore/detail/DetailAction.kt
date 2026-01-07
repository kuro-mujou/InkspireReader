package com.inkspire.ebookreader.ui.home.explore.detail

import com.inkspire.ebookreader.domain.model.ScrapedBookInfo


sealed interface DetailAction {
    data object NavigateBack : DetailAction
    data class DownloadBook(val book: ScrapedBookInfo) : DetailAction
}