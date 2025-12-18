package com.inkspire.ebookreader.ui.bookwriter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inkspire.ebookreader.navigation.Navigator

@Composable
fun BookWriterScreen(
    bookId: String,
    parentNavigator: Navigator,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Book Writer Screen")
    }
}