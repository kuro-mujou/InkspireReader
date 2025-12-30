package com.inkspire.ebookreader.ui.home.explore.detail

import com.inkspire.ebookreader.common.ScrapedBookInfo
import com.inkspire.ebookreader.common.UiState

data class DetailState (
    val searchResultDetail: UiState<ScrapedBookInfo> = UiState.None,
)