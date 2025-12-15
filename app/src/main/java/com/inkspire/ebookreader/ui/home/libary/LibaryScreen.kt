package com.inkspire.ebookreader.ui.home.libary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.inkspire.ebookreader.navigation.Route

@Composable
fun LibraryScreen(
    paddingValues: PaddingValues,
    onClick: (NavKey) -> Unit
) {
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
}