package com.inkspire.ebookreader.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.inkspire.ebookreader.navigation.Navigator
import com.inkspire.ebookreader.navigation.Route
import com.inkspire.ebookreader.navigation.rememberNavigator
import com.inkspire.ebookreader.ui.home.composable.MyNavigationSuiteScaffold
import com.inkspire.ebookreader.ui.home.explore.detail.DetailAction
import com.inkspire.ebookreader.ui.home.explore.detail.DetailScreen
import com.inkspire.ebookreader.ui.home.explore.detail.DetailViewModel
import com.inkspire.ebookreader.ui.home.explore.search.ExploreSearchScreen
import com.inkspire.ebookreader.ui.home.explore.search.SearchAction
import com.inkspire.ebookreader.ui.home.explore.search.SearchViewModel
import com.inkspire.ebookreader.ui.home.libary.LibraryRootScreen
import com.inkspire.ebookreader.ui.home.recentbook.RecentBookRootScreen
import com.inkspire.ebookreader.ui.home.setting.SettingRootScreen
import com.inkspire.ebookreader.ui.home.test.CustomToolbarContent
import com.inkspire.ebookreader.util.BookImporter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeScreen(
    parentNavigator: Navigator
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val homeNavigator = rememberNavigator(Route.Home.RecentBooks)
    BackHandler(enabled = homeNavigator.currentTab != Route.Home.RecentBooks) {
        homeNavigator.handleBack()
    }
    MyNavigationSuiteScaffold(
        homeNavigator = homeNavigator
    ) {
        NavDisplay(
            backStack = homeNavigator.backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<Route.Home.RecentBooks> {
                    RecentBookRootScreen(
                        parentNavigatorAction = parentNavigator::navigateTo,
                        homeNavigatorAction = homeNavigator::switchTab,
                    )
                }
                entry<Route.Home.Library> {
                    LibraryRootScreen(
                        homeNavigatorAction = homeNavigator::switchTab,
                        parentNavigatorAction = parentNavigator::navigateTo
                    )
                }
                entry<Route.Home.Explore.Search> {
                    val searchViewModel = koinViewModel<SearchViewModel>()
                    val exploreState by searchViewModel.state.collectAsStateWithLifecycle()
                    val isConnected by searchViewModel.isConnected.collectAsStateWithLifecycle()
                    ExploreSearchScreen(
                        searchState = exploreState,
                        isConnected = isConnected,
                        onAction = {
                            when (it) {
                                is SearchAction.PerformSearchBookDetail -> {
                                    homeNavigator.navigateTo(Route.Home.Explore.Detail(it.bookUrl, it.chapter))
                                }

                                else -> {
                                    searchViewModel.onAction(it)
                                }
                            }
                        }
                    )
                }
                entry<Route.Home.Explore.Detail> { params ->
                    val detailViewModel = koinViewModel<DetailViewModel>(parameters = { parametersOf(params.bookUrl) })
                    val detailState by detailViewModel.state.collectAsStateWithLifecycle()
                    DetailScreen(
                        detailState = detailState,
                        chapterInfo = params.chapter,
                        onAction = {
                            when (it) {
                                is DetailAction.NavigateBack -> {
                                    homeNavigator.handleBack()
                                }
                                is DetailAction.DownloadBook -> {
                                    BookImporter(
                                        context = context,
                                        scope = scope,
                                        specialIntent = "null"
                                    ).importTruyenFullBook(params.bookUrl, it.book)
                                    homeNavigator.handleBack()
                                    homeNavigator.switchTab(Route.Home.Library)
                                }
                            }
                        }
                    )
                }
                entry<Route.Home.Settings> {
                    SettingRootScreen()
                }
                entry<Route.Home.Test> {
                    CustomToolbarContent()
                }
            }
        )
    }
}