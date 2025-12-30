package com.inkspire.ebookreader.ui.home.explore.truyenfull

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.ui.home.explore.common.truyenFullCategories
import com.inkspire.ebookreader.ui.home.explore.truyenfull.composable.SearchedBookItem

@Composable
fun TruyenFullRoot(
    truyenFullState: TruyenFullState,
    onAction: (TruyenFullAction) -> Unit
) {
    when (val result = truyenFullState.searchResult) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = result.throwable.message ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
        is UiState.Empty -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results found", style = MaterialTheme.typography.bodyLarge)
            }
        }
        is UiState.None -> {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = truyenFullCategories
                ) {
                    OutlinedButton (
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        onClick = {
                            onAction(TruyenFullAction.PerformSearchCategory(it.second))
                        }
                    ) {
                        Text(it.first)
                    }
                }
            }
        }
        is UiState.Success -> {
            val searchedBooks = result.data
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onAction(TruyenFullAction.PreviousPage) },
                            enabled = truyenFullState.currentPage > 1
                        ) {
                            Icon(
                                modifier = Modifier.rotate(180f),
                                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                                contentDescription = "Previous Page"
                            )
                        }

                        Text(
                            text = "Page ${truyenFullState.currentPage} / ${truyenFullState.maxPage}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row {
                            IconButton(
                                onClick = { onAction(TruyenFullAction.NextPage) },
                                enabled = truyenFullState.currentPage < truyenFullState.maxPage
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                                    contentDescription = "Next Page"
                                )
                            }

                            IconButton(
                                onClick = { onAction(TruyenFullAction.ClearResult) }
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
                    SearchedBookItem(book)
                }
            }
        }
    }
}