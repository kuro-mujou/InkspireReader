package com.inkspire.ebookreader.ui.home.explore

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.composable.MySearchBox
import com.inkspire.ebookreader.ui.home.explore.common.supportedWebsites
import com.inkspire.ebookreader.ui.home.explore.truyenfull.TruyenFullAction
import com.inkspire.ebookreader.ui.home.explore.truyenfull.TruyenFullRoot
import com.inkspire.ebookreader.ui.home.explore.truyenfull.TruyenFullViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreRootScreen (

) {
    val exploreViewModel = koinViewModel<ExploreViewModel>()
    val exploreState by exploreViewModel.state.collectAsStateWithLifecycle()
    val truyenFullViewModel = koinViewModel<TruyenFullViewModel>()
    val truyenFullState by truyenFullViewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible
    var searchInput by remember { mutableStateOf("") }
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
            )
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = WindowInsets.systemBars
                    .union(WindowInsets.displayCutout)
                    .asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {
            stickyHeader {
                Text("Website:")
            }
            items(supportedWebsites) {
                FilterChip(
                    onClick = {
                        exploreViewModel.onAction(ExploreAction.ChangeSelectedWebsite(it))
                    },
                    label = {
                        Text(it)
                    },
                    selected = it == exploreState.selectedWebsite,
                    leadingIcon = if (it == exploreState.selectedWebsite) {
                        {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_confirm),
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }

        MySearchBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = searchInput,
            onValueChange = { newValue ->
                searchInput = newValue
            },
            hint = {
                Text("Search")
            },
            decorationAlwaysVisible = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchInput.isNotBlank()) {
                        if (exploreState.selectedWebsite == supportedWebsites[0]) {
                            truyenFullViewModel.onAction(TruyenFullAction.PerformSearchQuery(searchInput))
                        }
                        focusManager.clearFocus()
                    }
                }
            ),
            trailingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                    contentDescription = "Search icon",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            truyenFullViewModel.onAction(TruyenFullAction.PerformSearchQuery(searchInput))
                            focusManager.clearFocus()
                        }
                )
            }
        )

        Crossfade(targetState = exploreState.selectedWebsite) { targetState ->
            when (targetState) {
                supportedWebsites[0] -> {
                    TruyenFullRoot(
                        truyenFullState = truyenFullState,
                        onAction = truyenFullViewModel::onAction
                    )
                }

                supportedWebsites[1] -> {

                }
            }
        }
    }
}