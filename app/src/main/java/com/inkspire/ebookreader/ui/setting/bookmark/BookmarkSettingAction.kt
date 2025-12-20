package com.inkspire.ebookreader.ui.setting.bookmark

import com.inkspire.ebookreader.common.BookmarkStyle

sealed interface BookmarkSettingAction {
    data class UpdateSelectedBookmarkStyle(val bookmarkStyle: BookmarkStyle) : BookmarkSettingAction
}