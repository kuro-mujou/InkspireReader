package com.inkspire.ebookreader.ui.home.explore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.inkspire.ebookreader.navigation.Route
import com.inkspire.ebookreader.navigation.rememberNavigator
import com.inkspire.ebookreader.ui.home.explore.detail.DetailScreen
import com.inkspire.ebookreader.ui.home.explore.detail.DetailViewModel
import com.inkspire.ebookreader.ui.home.explore.search.ExploreSearchScreen
import com.inkspire.ebookreader.ui.home.explore.search.SearchAction
import com.inkspire.ebookreader.ui.home.explore.search.SearchViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreRootScreen(

) {
    val config = remember {
        SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(baseClass = NavKey::class) {
                    subclass(serializer = Route.Home.Explore.Search.serializer())
                    subclass(serializer = Route.Home.Explore.Detail.serializer())
                }
            }
        }
    }
    val exploreNavigator = rememberNavigator(config, Route.Home.Explore.Search)
    BackHandler(enabled = exploreNavigator.currentScreen != Route.Home.Explore.Search) {
        exploreNavigator.handleBack()
    }
    NavDisplay(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateTopPadding(),
                end = WindowInsets.systemBars
                    .union(WindowInsets.displayCutout)
                    .asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr)
            ),
        backStack = exploreNavigator.backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Route.Home.Explore.Search> {
                val searchViewModel = koinViewModel<SearchViewModel>()
                val exploreState by searchViewModel.state.collectAsStateWithLifecycle()
                ExploreSearchScreen(
                    searchState = exploreState,
                    onAction = {
                        when (it) {
                            is SearchAction.PerformSearchBookDetail -> {
                                exploreNavigator.navigateTo(Route.Home.Explore.Detail(it.bookUrl))
                            }

                            else -> {
                                searchViewModel.onAction(it)
                            }
                        }
                    }
                )
            }
            entry<Route.Home.Explore.Detail> {
                val detailViewModel = koinViewModel<DetailViewModel>(parameters = { parametersOf(it.bookUrl) })
                val detailState by detailViewModel.state.collectAsStateWithLifecycle()
                DetailScreen(
                    detailState = detailState,
                    onAction = detailViewModel::onAction
                )
            }
        }
    )
}