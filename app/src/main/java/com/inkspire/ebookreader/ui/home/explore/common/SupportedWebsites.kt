package com.inkspire.ebookreader.ui.home.explore.common

enum class SupportedWebsite(
    val displayName: String,
    val categories: List<WebsiteCategory>
) {

    TRUYEN_FULL(
        displayName = "Truyện Full",
        categories = TruyenFullCategory.entries
    ),

    TANG_THU_VIEN(
        displayName = "Tàng Thư Viện",
        categories = emptyList()
    );
}