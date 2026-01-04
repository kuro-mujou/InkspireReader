package com.inkspire.ebookreader.ui.setting.bookmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.domain.model.BookmarkMenuItem
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.setting.bookmark.composable.MyBookmarkItemView
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarkSetting(
    stylingState: StylingState?,
) {
    val viewModel = koinViewModel<BookmarkSettingViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val items = remember {
        listOf(
            BookmarkMenuItem(1, "Wavy style", BookmarkStyle.WAVE_WITH_BIRDS),
            BookmarkMenuItem(2, "Cloudy style", BookmarkStyle.CLOUD_WITH_BIRDS),
            BookmarkMenuItem(3, "Starry night", BookmarkStyle.STARRY_NIGHT),
            BookmarkMenuItem(4, "Geometric triangle", BookmarkStyle.GEOMETRIC_TRIANGLE),
            BookmarkMenuItem(5, "Polygonal hexagon", BookmarkStyle.POLYGONAL_HEXAGON),
            BookmarkMenuItem(6, "Scattered hexagon", BookmarkStyle.SCATTERED_HEXAGON),
            BookmarkMenuItem(7, "Cherry Blossom rain", BookmarkStyle.CHERRY_BLOSSOM_RAIN),
        )
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = stylingState?.stylePreferences?.backgroundColor ?: MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets.safeContent
                        .only(WindowInsetsSides.Horizontal)
                        .asPaddingValues()
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center),
                    text = "BOOKMARK STYLE",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = stylingState?.stylePreferences?.textColor ?: MaterialTheme.colorScheme.onSurface,
                        fontFamily = stylingState?.fontFamilies?.get(stylingState.stylePreferences.fontFamily)
                    )
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 8.dp),
                state = listState,
                content = {
                    items(
                        items = items,
                        key = { it.id }
                    ) { listItem ->
                        MyBookmarkItemView(
                            state = state,
                            listItem = listItem,
                            stylingState = stylingState,
                            onSelected = {
                                viewModel.onAction(
                                    BookmarkSettingAction.UpdateSelectedBookmarkStyle(
                                        listItem.bookmarkStyle
                                    )
                                )
                            }
                        )
                    }
                }
            )
        }
    }
}