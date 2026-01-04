package com.inkspire.ebookreader.ui.home.explore.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.ui.composable.MySearchBox
import com.inkspire.ebookreader.ui.home.explore.common.SupportedWebsite
import com.inkspire.ebookreader.ui.home.explore.search.composable.SearchedBookItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreSearchScreen(
    searchState: SearchState,
    isConnected: Boolean,
    onAction: (SearchAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible
    val searchBoxState = rememberTextFieldState()
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }
    Column(
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
            )
    ) {
        if (isConnected) {
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
                items(SupportedWebsite.entries) {
                    FilterChip(
                        onClick = {
                            onAction(SearchAction.ChangeSelectedWebsite(it))
                            searchBoxState.clearText()
                            focusManager.clearFocus()
                        },
                        label = {
                            Text(it.displayName)
                        },
                        selected = it == searchState.selectedWebsite,
                        leadingIcon = if (it == searchState.selectedWebsite) {
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
                textFieldState = searchBoxState,
                hint = {
                    Text("Search")
                },
                decorationAlwaysVisible = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = {
                    if (searchBoxState.text.isNotBlank()) {
                        when (searchState.selectedWebsite) {
                            SupportedWebsite.TRUYEN_FULL -> {
                                onAction(SearchAction.PerformSearchQuery(searchBoxState.text.toString()))
                            }
//                        SupportedWebsite.TANG_THU_VIEN -> {
//
//                        }
                        }
                        focusManager.clearFocus()
                    }
                },
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
                                onAction(SearchAction.PerformSearchQuery(searchBoxState.text.toString()))
                                focusManager.clearFocus()
                            }
                    )
                }
            )

            when (val result = searchState.searchResult) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = result.throwable.message ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is UiState.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No results found", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                is UiState.None -> {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(searchState.selectedWebsite.categories) {
                            OutlinedButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                onClick = {
                                    onAction(SearchAction.PerformSearchCategory(it.slug))
                                }
                            ) {
                                Text(it.displayName)
                            }
                        }
                    }
                }

                is UiState.Success -> {
                    val searchedBooks = result.data()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onAction(SearchAction.PreviousPage) },
                                    enabled = searchState.currentPage > 1
                                ) {
                                    Icon(
                                        modifier = Modifier.rotate(180f),
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                                        contentDescription = "Previous Page"
                                    )
                                }

                                Text(
                                    text = "Page ${searchState.currentPage} / ${searchState.maxPage}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Row {
                                    IconButton(
                                        onClick = { onAction(SearchAction.NextPage) },
                                        enabled = searchState.currentPage < searchState.maxPage
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                                            contentDescription = "Next Page"
                                        )
                                    }

                                    IconButton(
                                        onClick = { onAction(SearchAction.ClearResult) }
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_cancel),
                                            contentDescription = "Close Search",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            HorizontalDivider()
                        }

                        items(items = searchedBooks) { book ->
                            SearchedBookItem(
                                searchedBook = book,
                                onAction = onAction
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No internet connection",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please check your internet connection and try again",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}