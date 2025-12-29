package com.inkspire.ebookreader.ui.home.explore

sealed interface ExploreAction {
    data class ChangeSelectedWebsite(val selectedWebsite: String) : ExploreAction
}