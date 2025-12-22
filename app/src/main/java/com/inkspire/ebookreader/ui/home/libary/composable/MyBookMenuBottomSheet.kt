package com.inkspire.ebookreader.ui.home.libary.composable

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.inkspire.ebookreader.domain.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookMenuBottomSheet(
    book: Book?,
    sheetState: SheetState,
    onDismiss: (Book) -> Unit,
    onViewBookDetails: (Book) -> Unit,
    onDeleteBook: (Book) -> Unit,
) {
    book?.let { bookInfo ->
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss(bookInfo)
            },
            sheetState = sheetState,
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
        ) {
            val view = LocalView.current
            SideEffect {
                val window = (view.context as? Activity)?.window ?: (view.parent as? DialogWindowProvider)?.window
                window?.let {
                    if (Build.VERSION.SDK_INT >= 29) {
                        it.isNavigationBarContrastEnforced = false
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
                    .padding(
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
                    ),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = bookInfo.title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        fontWeight = FontWeight.Medium
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bookInfo.authors.joinToString(","),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Normal
                    )
                )
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDismiss(bookInfo)
                        onViewBookDetails(bookInfo)
                    }
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "•View Book Details",
                            textAlign = TextAlign.Start
                        )
                    }
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDismiss(bookInfo)
                        onDeleteBook(bookInfo)
                    }
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "•Delete",
                            style = TextStyle(
                                color = Color.Red
                            ),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}