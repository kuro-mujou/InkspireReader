package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R

@Composable
fun SelectionMenu(
    onHighlight: (Color) -> Unit,
    onAddNote: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row {
            IconButton(onClick = { onHighlight(Color.Red) }) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Red, shape = CircleShape)
                        .border(2.dp, Color.Black, shape = CircleShape)
                )
            }
            IconButton(onClick = { onHighlight(Color.Cyan) }) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Cyan, shape = CircleShape)
                        .border(2.dp, Color.Black, shape = CircleShape)
                )
            }
            IconButton(onClick = { onHighlight(Color.Magenta) }) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Magenta, shape = CircleShape)
                        .border(2.dp, Color.Black, shape = CircleShape)
                )
            }
            IconButton(onClick = onAddNote) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_comment),
                    contentDescription = "Note",
                    tint = Color.Black
                )
            }
        }
    }
}