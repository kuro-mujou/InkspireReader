package com.inkspire.ebookreader.ui.home.libary

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.BookImporter
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.common.UiState
import com.inkspire.ebookreader.domain.model.MiniFabItem
import com.inkspire.ebookreader.navigation.Route
import com.inkspire.ebookreader.ui.composable.MyBookChip
import com.inkspire.ebookreader.ui.composable.MyBookMenuBottomSheet
import com.inkspire.ebookreader.ui.composable.MyDriveInputLinkDialog
import com.inkspire.ebookreader.ui.composable.MyExpandableFab
import com.inkspire.ebookreader.ui.composable.MyGridBookView
import com.inkspire.ebookreader.ui.composable.MyListBookView
import com.inkspire.ebookreader.ui.composable.MyLoadingAnimation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LibraryScreen(
    state: LibraryState,
    onAction: (LibraryAction) -> Unit,
    parentNavigatorAction: (NavKey) -> Unit
) {
    val context = LocalContext.current
//    val focusManager = LocalFocusManager.current
    val isKeyboardVisible = WindowInsets.isImeVisible
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val gridState = rememberLazyStaggeredGridState()
    val listState = rememberLazyListState()
    val searchState = rememberTextFieldState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var specialIntent by remember { mutableStateOf("null") }
    var fabState by remember { mutableStateOf(true) }
    var fabExpandedState by remember { mutableStateOf(false) }
    var dropdownMenuState by remember { mutableStateOf(false) }
    var driveDialogState by remember { mutableStateOf(false) }
    var bottomSheetState by remember { mutableStateOf(false) }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val columnsStaggeredGridCount by remember(deviceConfiguration) {
        mutableStateOf(
            when (deviceConfiguration) {
                DeviceConfiguration.PHONE_PORTRAIT -> StaggeredGridCells.Fixed(2)
                DeviceConfiguration.TABLET_PORTRAIT -> StaggeredGridCells.Fixed(3)
                DeviceConfiguration.PHONE_LANDSCAPE -> StaggeredGridCells.Fixed(3)
                DeviceConfiguration.TABLET_LANDSCAPE -> StaggeredGridCells.Fixed(4)
            }
        )
    }
    val importBookLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        BookImporter(
            context = context,
            scope = scope,
            specialIntent = specialIntent
        ).processIntentUri(uri)
    }

    val fabItems = listOf(
        MiniFabItem(
            icon = R.drawable.ic_add_epub,
            title = "Import EPUB",
            tint = if (isSystemInDarkTheme())
                Color(255, 250, 160)
            else
                Color(131, 105, 83),
            onClick = {
                specialIntent = "null"
                fabExpandedState = false
                importBookLauncher.launch(arrayOf("application/epub+zip"))
            }
        ),
        MiniFabItem(
            icon = R.drawable.ic_add_epub,
            title = "Import CBZ",
            tint = if (isSystemInDarkTheme())
                Color(255, 250, 160)
            else
                Color(131, 105, 83),
            onClick = {
                specialIntent = "null"
                fabExpandedState = false
                importBookLauncher.launch(arrayOf("application/vnd.comicbook+zip", "application/octet-stream"))
            }
        ),
        MiniFabItem(
            icon = R.drawable.ic_add_epub,
            title = "Import PDF with page render",
            tint = if (isSystemInDarkTheme())
                Color(255, 250, 160)
            else
                Color(131, 105, 83),
            onClick = {
                specialIntent = "PAGE"
                fabExpandedState = false
                importBookLauncher.launch(
                    arrayOf("application/pdf")
                )
            }
        ),
        MiniFabItem(
            icon = R.drawable.ic_add_epub,
            title = "Import PDF with text/image extraction",
            tint = if (isSystemInDarkTheme())
                Color(255, 250, 160)
            else
                Color(131, 105, 83),
            onClick = {
                specialIntent = "TEXT"
                fabExpandedState = false
                importBookLauncher.launch(
                    arrayOf("application/pdf")
                )
            }
        ),
        MiniFabItem(
            icon = R.drawable.ic_add_epub,
            title = "Import EPUB via Google Drive",
            tint = if (isSystemInDarkTheme())
                Color(255, 250, 160)
            else
                Color(131, 105, 83),
            onClick = {
                specialIntent = "null"
                fabExpandedState = false
                driveDialogState = true
            }
        ),
        MiniFabItem(
            icon = R.drawable.ic_write_ebook,
            title = "Write new Book",
            tint = if (isSystemInDarkTheme())
                Color(155, 212, 161)
            else
                Color(52, 105, 63),
            onClick = {
                fabExpandedState = false
                parentNavigatorAction(Route.BookWriter(""))
            }
        )
    )

//    LaunchedEffect(Unit) {
//        focusManager.clearFocus()
//    }
//    LaunchedEffect(isKeyboardVisible) {
//        if (!isKeyboardVisible) {
//            focusManager.clearFocus()
//        }
//    }
    LaunchedEffect(drawerState.currentValue) {
        fabState = drawerState.isClosed
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl ) {
        ModalNavigationDrawer(
            gesturesEnabled = drawerState.isOpen,
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr){
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = WindowInsets.safeContent
                                        .only(WindowInsetsSides.Top)
                                        .asPaddingValues()
                                        .calculateTopPadding(),
                                    end = WindowInsets.safeContent
                                        .only(WindowInsetsSides.End)
                                        .asPaddingValues()
                                        .calculateEndPadding(LayoutDirection.Ltr)
                                )
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(all = 4.dp)
                                    .fillMaxWidth(),
                                text = "Filter",
                                style = TextStyle(
                                    textAlign = TextAlign.Center
                                )
                            )
                            FlowRow(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(all = 8.dp)
                                    .verticalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.categories.forEach { categoryChip ->
                                    MyBookChip(
                                        selected = categoryChip.isSelected,
                                        color = Color(categoryChip.color),
                                        onClick = {
                                            onAction(LibraryAction.ChangeChipState(categoryChip))
                                        }
                                    ) {
                                        Text(text = categoryChip.name)
                                    }
                                }
                            }
                            if (!state.categories.none { it.isSelected }){
                                Button(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally),
                                    onClick = {
                                        onAction(LibraryAction.ResetChipState)
                                    }
                                ) {
                                    Text(text = "Reset")
                                }
                            }
                        }
                    }
                }
            },
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr){
                    Column(
                        modifier = Modifier
                            .padding(
                                top = WindowInsets.safeContent
                                    .only(WindowInsetsSides.Top)
                                    .asPaddingValues()
                                    .calculateTopPadding(),
                                end = WindowInsets.safeContent
                                    .only(WindowInsetsSides.End)
                                    .asPaddingValues()
                                    .calculateEndPadding(LayoutDirection.Ltr)
                            ),


//                        .clickable(
//                            indication = null,
//                    interactionSource = remember { MutableInteractionSource() },
//                    onClick = { focusManager.clearFocus() }
//                    )
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Crossfade(state.isOnDeletingBooks) { isDeleting ->
                            when (isDeleting) {
                                false -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        IconButton(
                                            onClick = {
                                                onAction(LibraryAction.UpdateBookListType)
                                            }
                                        ) {
                                            if (state.listViewType == 1) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic_grid_view),
                                                    contentDescription = "Grid",
                                                    tint = if (isSystemInDarkTheme())
                                                        Color(154, 204, 250)
                                                    else
                                                        Color(45, 98, 139)
                                                )
                                            } else if (state.listViewType == 0) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic_list_view),
                                                    contentDescription = "List",
                                                    tint = if (isSystemInDarkTheme())
                                                        Color(154, 204, 250)
                                                    else
                                                        Color(45, 98, 139)
                                                )
                                            }
                                        }
                                        OutlinedTextField(
                                            state = searchState,
                                            modifier = Modifier
                                                .weight(1f),
                                            placeholder = {
                                                Text(text = "Search")
                                            },
                                            lineLimits = TextFieldLineLimits.SingleLine,
                                            shape = RoundedCornerShape(25.dp)
                                        )
                                        Box {
                                            IconButton(onClick = { dropdownMenuState = !dropdownMenuState }) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.ic_setting),
                                                    contentDescription = "More options"
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = dropdownMenuState,
                                                onDismissRequest = { dropdownMenuState = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Delete") },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
                                                            contentDescription = "Delete Icon",
                                                            tint = if (isSystemInDarkTheme())
                                                                Color(250, 160, 160)
                                                            else
                                                                Color(194, 59, 34)
                                                        )
                                                    },
                                                    onClick = {
                                                        onAction(LibraryAction.UpdateDeletingState)
                                                        dropdownMenuState = false
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Sort") },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = ImageVector.vectorResource(R.drawable.ic_bookmark_star),
                                                            contentDescription = "Sorting Icon",
                                                            tint = if (state.isSortedByFavorite)
                                                                if (isSystemInDarkTheme())
                                                                    Color(155, 212, 161)
                                                                else
                                                                    Color(52, 105, 63)
                                                            else Color.Gray
                                                        )
                                                    },
                                                    onClick = {
                                                        onAction(LibraryAction.UpdateSortState)
                                                        dropdownMenuState = false
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Filter") },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = ImageVector.vectorResource(R.drawable.ic_filter),
                                                            contentDescription = "Filter Icon",
                                                            tint = if (isSystemInDarkTheme())
                                                                Color(255, 250, 160)
                                                            else
                                                                Color(131, 105, 83),
                                                        )
                                                    },
                                                    onClick = {
                                                        scope.launch {
                                                            dropdownMenuState = false
                                                            drawerState.open()
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                true -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        IconButton(
                                            onClick = {
                                                onAction(LibraryAction.ConfirmDeleteBooks)
                                                onAction(LibraryAction.UpdateDeletingState)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.ic_confirm),
                                                contentDescription = "Confirm Delete Icon",
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                onAction(LibraryAction.UpdateDeletingState)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.ic_cancel),
                                                contentDescription = "Cancel Delete Icon",
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        when(val bookListState = state.bookList) {
                            is UiState.None -> {

                            }
                            is UiState.Loading -> {
                                MyLoadingAnimation()
                            }
                            is UiState.Empty -> {
                                Text(text = "No books found")
                            }
                            is UiState.Error -> {
                                Text(text = "Error loading books")
                            }
                            is UiState.Success -> {
                                Crossfade(targetState = state.listViewType) { option ->
                                    val sortedBooks = bookListState.data
                                        .filter {
                                            it.title.contains(searchState.text, ignoreCase = true) ||
                                                    it.authors.joinToString(",").contains(searchState.text, ignoreCase = true)
                                        }
                                        .let { list ->
                                            if (state.isSortedByFavorite)
                                                list.sortedByDescending { it.isFavorite }
                                            else
                                                list
                                        }
                                    when (option) {
                                        -1 -> {
                                            Box(modifier = Modifier.fillMaxSize())
                                        }
                                        0 -> {
                                            LazyColumn(
                                                state = listState,
                                                modifier = Modifier
                                                    .fillMaxSize(),
                                            ) {
                                                items(
                                                    items = sortedBooks,
                                                    key = { it.id }
                                                ) {
                                                    MyListBookView(
                                                        book = it,
                                                        libraryState = state,
                                                        onItemClick = {
                                                            if (!state.isOnDeletingBooks) {
                                                                parentNavigatorAction(Route.BookContent(it.id))
                                                            }
                                                        },
                                                        onItemLongClick = {
                                                            if (!state.isOnDeletingBooks) {
                                                                onAction(LibraryAction.AddSelectedBook(it))
                                                                bottomSheetState = true
                                                                scope.launch {
                                                                    sheetState.show()
                                                                }
                                                            }
                                                        },
                                                        onItemDoubleClick = {
                                                            if (!state.isOnDeletingBooks) {
                                                                parentNavigatorAction(Route.BookDetail(it.id))
                                                            }
                                                        },
                                                        onItemStarClick = {
                                                            onAction(LibraryAction.UpdateBookFavoriteState(it))
                                                        },
                                                        onItemCheckBoxClick = { isChecked ->
                                                            if (isChecked)
                                                                onAction(LibraryAction.AddSelectedBook(it))
                                                            else
                                                                onAction(LibraryAction.RemoveSelectedBook(it))
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                        1 -> {
                                            LazyVerticalStaggeredGrid(
                                                columns = columnsStaggeredGridCount,
                                                verticalItemSpacing = 8.dp,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(all = 8.dp),
                                                state = gridState,
                                                content = {
                                                    items(
                                                        items = sortedBooks,
                                                        key = { it.id }
                                                    ) {
                                                        MyGridBookView(
                                                            book = it,
                                                            libraryState = state,
                                                            onItemClick = {
                                                                if (!state.isOnDeletingBooks) {
                                                                    parentNavigatorAction(Route.BookContent(it.id))
                                                                }
                                                            },
                                                            onItemLongClick = {
                                                                if (!state.isOnDeletingBooks) {
                                                                    onAction(LibraryAction.AddSelectedBook(it))
                                                                    bottomSheetState = true
                                                                    scope.launch {
                                                                        sheetState.show()
                                                                    }
                                                                }
                                                            },
                                                            onItemDoubleClick = {
                                                                if (!state.isOnDeletingBooks) {
                                                                    parentNavigatorAction(Route.BookDetail(it.id))
                                                                }
                                                            },
                                                            onItemStarClick = {
                                                                onAction(LibraryAction.UpdateBookFavoriteState(it))
                                                            },
                                                            onItemCheckBoxClick = { isChecked ->
                                                                if (isChecked)
                                                                    onAction(LibraryAction.AddSelectedBook(it))
                                                                else
                                                                    onAction(LibraryAction.RemoveSelectedBook(it))
                                                            }
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    AnimatedVisibility(
        visible = bottomSheetState,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        MyBookMenuBottomSheet(
            book = state.selectedBookList.firstOrNull(),
            sheetState = sheetState,
            onDismiss = {
                bottomSheetState = false
                onAction(LibraryAction.RemoveSelectedBook(it))
            },
            onViewBookDetails = {
                onAction(LibraryAction.RemoveSelectedBook(it))
                parentNavigatorAction(Route.BookDetail(it.id))
            },
            onDeleteBook = {
                onAction(LibraryAction.DeleteSelectedBooks(it))
                onAction(LibraryAction.RemoveSelectedBook(it))
            }
        )
    }

    AnimatedVisibility(
        visible = driveDialogState,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        MyDriveInputLinkDialog(
            onDismiss = { driveDialogState = false },
            onConfirm = { link ->
                BookImporter(
                    context = context,
                    scope = scope,
                    specialIntent = "null"
                ).importBookViaGoogleDrive(link)
            }
        )
    }

    AnimatedVisibility(
        visible = fabState,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        MyExpandableFab(
            items = fabItems,
            expanded = fabExpandedState,
            deviceConfiguration = deviceConfiguration,
            onToggle = { fabExpandedState = !fabExpandedState },
            onDismiss = { fabExpandedState = false }
        )
    }
}