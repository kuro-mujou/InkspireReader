package com.inkspire.ebookreader.ui.root

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.inkspire.ebookreader.common.BookImporter
import com.inkspire.ebookreader.navigation.Route
import com.inkspire.ebookreader.navigation.rememberNavigator
import com.inkspire.ebookreader.ui.bookcontent.BookContentScreen
import com.inkspire.ebookreader.ui.bookdetail.BookDetailScreen
import com.inkspire.ebookreader.ui.bookwriter.BookWriterScreen
import com.inkspire.ebookreader.ui.home.HomeScreen
import com.inkspire.ebookreader.ui.theme.Bookshelf3Theme
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIncomingFile(intent)
        setContent {
            Bookshelf3Theme {
                val factory = rememberPermissionsControllerFactory()
                val controller = remember(factory) { factory.createPermissionsController() }
                val coroutineScope = rememberCoroutineScope()
                BindEffect(controller)
                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        try {
                            controller.providePermission(Permission.REMOTE_NOTIFICATION)
                        } catch (_: Exception) {}
                    }
                }

                val config = remember {
                    SavedStateConfiguration {
                        serializersModule = SerializersModule {
                            polymorphic(baseClass = NavKey::class) {
                                subclass(serializer = Route.Home.serializer())
                                subclass(serializer = Route.BookDetail.serializer())
                                subclass(serializer = Route.BookContent.serializer())
                                subclass(serializer = Route.BookWriter.serializer())
                            }
                        }
                    }
                }
                val navigator = rememberNavigator(config, Route.Home)

                BackHandler(enabled = true) {
                    if (!navigator.handleBack()) finish()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { _ ->
                    NavDisplay(
                        backStack = navigator.backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                        entryProvider = entryProvider {
                            entry<Route.Home> {
                                HomeScreen(
                                    parentNavigator = navigator
                                )
                            }
                            entry<Route.BookContent> { entry ->
                                BookContentScreen(
                                    bookId = entry.bookId
                                )
                            }
                            entry<Route.BookDetail> { entry ->
                                BookDetailScreen(
                                    bookId = entry.bookId
                                )
                            }
                            entry<Route.BookWriter> { entry ->
                                BookWriterScreen(
                                    bookId = entry.bookId
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingFile(intent)
    }

    private fun handleIncomingFile(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            if (uri != null) {
                BookImporter(
                    context = this,
                    scope = this.lifecycleScope,
                    specialIntent = "null"
                ).processIntentUri(uri)
            }
        }
    }
}