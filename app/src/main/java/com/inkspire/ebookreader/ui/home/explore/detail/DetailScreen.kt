package com.inkspire.ebookreader.ui.home.explore.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.ui.home.explore.detail.composable.DetailFooter
import com.inkspire.ebookreader.ui.home.explore.detail.composable.DetailHeader

@Composable
fun DetailScreen(
    detailState: DetailState,
    chapterInfo: String,
    onAction: (DetailAction) -> Unit,
) {
    when (val result = detailState.searchResultDetail) {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            )
        }

        is UiState.Success -> {
            val searchedBooks = result.data
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
            Column (
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
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (deviceConfiguration) {
                    DeviceConfiguration.PHONE_PORTRAIT,
                    DeviceConfiguration.TABLET_PORTRAIT -> {
                        Column {
                            DetailHeader(
                                deviceConfiguration = deviceConfiguration,
                                searchedBooks = searchedBooks,
                                chapterInfo = chapterInfo,
                                onAction = onAction
                            )
                            DetailFooter(
                                modifier = Modifier.weight(1f),
                                searchedBooks = searchedBooks
                            )
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(4.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    onAction(DetailAction.DownloadBook(searchedBooks))
                                }
                            ) {
                                Text("Download")
                            }
                        }
                    }
                    DeviceConfiguration.PHONE_LANDSCAPE,
                    DeviceConfiguration.TABLET_LANDSCAPE -> {
                        Row {
                            DetailHeader(
                                deviceConfiguration = deviceConfiguration,
                                searchedBooks = searchedBooks,
                                chapterInfo = chapterInfo,
                                onAction = onAction
                            )
                            DetailFooter(
                                modifier = Modifier.weight(1f),
                                searchedBooks = searchedBooks
                            )
                        }
                    }
                }
            }
        }
    }
}