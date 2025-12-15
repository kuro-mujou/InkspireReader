package com.inkspire.ebookreader.ui.home.libary

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.BookImporter
import com.inkspire.ebookreader.domain.model.MiniFabItem
import com.inkspire.ebookreader.navigation.Route
import com.inkspire.ebookreader.ui.composable.ExpandableFab

@Composable
fun LibraryScreen(
    paddingValues: PaddingValues,
    onClick: (NavKey) -> Unit
) {
    var specialIntent by remember { mutableStateOf("null") }
    var fabExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDriveInputLinkDialog by remember { mutableStateOf(false) }
    var showFab by remember { mutableStateOf(true) }
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
                fabExpanded = false
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
                fabExpanded = false
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
                fabExpanded = false
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
                fabExpanded = false
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
                fabExpanded = false
                showDriveInputLinkDialog = true
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
                fabExpanded = false
//                navigateTo(Route.WriteBook(""))
            }
        )
    )
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Library Screen")
        Button(
            onClick = {
                onClick(Route.BookDetail("1"))
            }
        ) {
            Text(text = "Navigate to Books Detail")
        }
        Button(
            onClick = {
                onClick(Route.BookContent("1", 1))
            }
        ) {
            Text(text = "Navigate to Books Content")
        }
        Button(
            onClick = {
                onClick(Route.BookWriter("1"))
            }
        ) {
            Text(text = "Navigate to Books Writer")
        }
    }

    AnimatedVisibility(
        visible = showFab,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ExpandableFab(
            items = fabItems,
            expanded = fabExpanded,
            onToggle = { fabExpanded = !fabExpanded },
            onDismiss = { fabExpanded = false }
        )
    }
}