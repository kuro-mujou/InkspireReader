package com.inkspire.ebookreader.ui.home.explore.detail

import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.ScrapedBookInfo

data class DetailState (
    val searchResultDetail: UiState<ScrapedBookInfo> = UiState.None,
)