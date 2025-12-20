package com.inkspire.ebookreader.ui.bookdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.navigation.Route
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookDetailFooter(
    state: BookDetailState,
    deviceConfiguration: DeviceConfiguration,
    onAction: (BookDetailAction) -> Unit,
    onNavigate: (NavKey) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    var searchInput by remember { mutableStateOf("") }
    var targetSearchIndex by remember { mutableIntStateOf(-1) }

    var enableSearch by remember { mutableStateOf(deviceConfiguration != DeviceConfiguration.PHONE_PORTRAIT) }
    val gridCells =
        if (deviceConfiguration == DeviceConfiguration.PHONE_PORTRAIT || deviceConfiguration == DeviceConfiguration.PHONE_LANDSCAPE)
            GridCells.Fixed(1)
        else
            GridCells.Fixed(2)

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                end = WindowInsets.systemBars
                    .union(WindowInsets.displayCutout)
                    .asPaddingValues()
                    .calculateEndPadding(layoutDirection = LayoutDirection.Ltr),
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .then(
                if (deviceConfiguration == DeviceConfiguration.PHONE_LANDSCAPE || deviceConfiguration == DeviceConfiguration.TABLET_LANDSCAPE)
                    Modifier.padding(
                        top = WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateTopPadding(),
                        start = 10.dp
                    )
                else
                    Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = gridCells,
            modifier = Modifier
                .weight(1f),
            state = lazyGridState
        ) {
            item(
                span = { GridItemSpan(if (gridCells == GridCells.Fixed(2)) 2 else 1) }
            ) {
                when (deviceConfiguration) {
                    DeviceConfiguration.PHONE_PORTRAIT,
                    DeviceConfiguration.PHONE_LANDSCAPE,
                    DeviceConfiguration.TABLET_PORTRAIT -> {
                        BookDetailExtraInfo(
                            modifier = Modifier,
                            state = state,
                            onAction = onAction
                        )
                    }

                    else -> {}
                }
            }
            stickyHeader {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        if (deviceConfiguration == DeviceConfiguration.PHONE_PORTRAIT) {
                            IconButton(
                                onClick = {
                                    enableSearch = !enableSearch
                                },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = if (enableSearch)
                                        ImageVector.vectorResource(R.drawable.ic_up)
                                    else
                                        ImageVector.vectorResource(R.drawable.ic_down),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Text(
                            text = "Table of Content",
                            modifier = Modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    AnimatedVisibility(
                        visible = enableSearch
                    ) {
                        OutlinedTextField(
                            value = searchInput,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() }) {
                                    searchInput = newValue
                                }
                            },
                            label = {
                                Text(
                                    text = "Enter a chapter number",
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val chapterIndex = searchInput.toIntOrNull()
                                    if (chapterIndex != null) {
                                        targetSearchIndex =
                                            if (chapterIndex < state.tableOfContents.size)
                                                chapterIndex
                                            else
                                                state.tableOfContents.size - 1
                                        scope.launch {
                                            lazyGridState.scrollToItem(targetSearchIndex)
                                        }
                                        searchInput = ""
//                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    }
                }
            }
            items(
                items = state.tableOfContents,
                key = { tocItem -> tocItem.index }
            ) { tocItem ->
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = tocItem.title,
                            style =
                                if (state.tableOfContents.indexOf(tocItem) == targetSearchIndex) {
                                    TextStyle(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                    )
                                } else {
                                    TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                    )
                                },
                        )
                    },
                    selected = state.tableOfContents.indexOf(tocItem) == targetSearchIndex,
                    onClick = {
                        onAction(BookDetailAction.OnDrawerItemClick(tocItem.index))
                        onNavigate(Route.BookContent(bookId = state.bookWithCategories?.book?.bookId ?: ""))
                    },
                    modifier = Modifier
                        .padding(4.dp, 2.dp, 4.dp, 2.dp)
                        .wrapContentHeight(),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = if (state.tableOfContents.indexOf(tocItem) == targetSearchIndex) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            Color.Transparent
                        },
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            }
        }
        when (deviceConfiguration) {
            DeviceConfiguration.PHONE_PORTRAIT,
            DeviceConfiguration.TABLET_PORTRAIT -> {
                Button(
                    onClick = {
                        onNavigate(
                            Route.BookContent(
                                bookId = state.bookWithCategories?.book?.bookId ?: ""
                            )
                        )
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .then(
                            if (deviceConfiguration == DeviceConfiguration.PHONE_PORTRAIT)
                                Modifier.fillMaxWidth()
                            else
                                Modifier.fillMaxWidth(0.6f)
                        )
                        .height(50.dp)
                ) {
                    Text(
                        text = "Read Book",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            else -> {}
        }
    }
}