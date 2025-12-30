package com.inkspire.ebookreader.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Home : Route, NavKey {

        @Serializable
        data object RecentBooks : Route, NavKey

        @Serializable
        data object Library : Route, NavKey

        @Serializable
        data object Explore : Route, NavKey {

            @Serializable
            data object Search : Route, NavKey

            @Serializable
            data class Detail(val bookUrl: String) : Route, NavKey
        }

        @Serializable
        data object Settings : Route, NavKey
    }

    @Serializable
    data class BookDetail(val bookId: String) : Route, NavKey

    @Serializable
    data class BookContent(val bookId: String) : Route, NavKey

    @Serializable
    data class BookWriter(val bookId: String) : Route, NavKey
}
