package com.inkspire.ebookreader.ui.home.explore.truyenfull

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.ui.home.explore.common.truyenFullCategories

@Composable
fun TruyenFullRoot(
    truyenFullState: TruyenFullState,
    onAction: (TruyenFullAction) -> Unit
) {
    when (truyenFullState.searchResult) {
        is UiState.Loading -> {

        }
        is UiState.Error -> {

        }
        is UiState.Empty, UiState.None -> {
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
            val result = truyenFullState.searchResult.data
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    result
                ) {

                }
            }
        }
    }
}