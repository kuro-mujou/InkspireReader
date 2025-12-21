package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun ImageComponent(
    stylingState: StylingState,
    uriString: String,
) {
    Card(
        modifier = Modifier
            .then(
                if (stylingState.imagePaddingState)
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                else
                    Modifier
            )
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RectangleShape
    ) {
        AsyncImage(
            model = uriString,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}