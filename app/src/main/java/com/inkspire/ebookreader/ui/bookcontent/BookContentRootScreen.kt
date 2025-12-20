package com.inkspire.ebookreader.ui.bookcontent

import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BookContentRootScreen(
    bookId: String,
    parentNavigator: Navigator
) {
    val viewModel = koinViewModel<BookContentViewModel>(parameters = { parametersOf(bookId) })
    val bookContentState by viewModel.bookContentState.collectAsStateWithLifecycle()
    val contentState by viewModel.contentState.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerContent = {

        },
        content = {
            BookContentScreen(
                bookContentState = bookContentState,
                contentState = contentState,
                onAction = viewModel::onAction
            )
        }
    )
}