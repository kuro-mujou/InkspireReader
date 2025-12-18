package com.inkspire.ebookreader.ui.bookdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun BookDetailScreenRoot(
    bookId: String,
    parentNavigator: Navigator,
) {
    val viewModel = koinViewModel<BookDetailViewModel>(
        parameters = { parametersOf(bookId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = parentNavigator::handleBack,
        onNavigate = parentNavigator::navigateTo
    )
}