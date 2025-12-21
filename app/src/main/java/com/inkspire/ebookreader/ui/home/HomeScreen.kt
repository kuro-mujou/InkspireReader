package com.inkspire.ebookreader.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.inkspire.ebookreader.navigation.Navigator
import com.inkspire.ebookreader.navigation.Route
import com.inkspire.ebookreader.navigation.rememberNavigator
import com.inkspire.ebookreader.ui.home.composable.MyNavigationSuiteScaffold
import com.inkspire.ebookreader.ui.home.libary.LibraryRootScreen
import com.inkspire.ebookreader.ui.home.recentbook.RecentBookRootScreen
import com.inkspire.ebookreader.ui.home.setting.SettingRootScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Composable
fun HomeScreen(
    parentNavigator: Navigator
) {
    val config = remember {
        SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(baseClass = NavKey::class) {
                    subclass(serializer = Route.Home.RecentBooks.serializer())
                    subclass(serializer = Route.Home.Library.serializer())
                    subclass(serializer = Route.Home.Explore.serializer())
                    subclass(serializer = Route.Home.Settings.serializer())
                }
            }
        }
    }
    val homeNavigator = rememberNavigator(config, Route.Home.RecentBooks)
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
                        parentNavigatorAction = parentNavigator::navigateTo
                    )
                }
                entry<Route.Home.Explore> {
                    //todo add explore screen for search truyenfull/tangthuvien
//                    ExploreRootScreen()
                }
                entry<Route.Home.Settings> {
                    SettingRootScreen()
                }
            }
        )
    }
}