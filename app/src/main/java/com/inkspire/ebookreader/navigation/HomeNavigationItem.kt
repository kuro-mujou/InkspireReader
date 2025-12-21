package com.inkspire.ebookreader.navigation

import androidx.navigation3.runtime.NavKey
import com.inkspire.ebookreader.R

data class HomeNavItem(
    val label: String,
    val icon: Int,
    val route: NavKey
)

val homeNavItems = listOf(
    HomeNavItem(
        label = "Recents",
        icon = R.drawable.ic_home,
        route = Route.Home.RecentBooks
    ),
    HomeNavItem(
        label = "Library",
        icon = R.drawable.ic_book_list,
        route = Route.Home.Library
    ),
    HomeNavItem(
        label = "Explore",
        icon = R.drawable.ic_book_list,
        route = Route.Home.Explore
    ),
    HomeNavItem(
        label = "Settings",
        icon = R.drawable.ic_setting,
        route = Route.Home.Settings
    )
)