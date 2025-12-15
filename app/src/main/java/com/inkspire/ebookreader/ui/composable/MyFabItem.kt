package com.inkspire.ebookreader.ui.composable

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.inkspire.ebookreader.domain.model.MiniFabItem

@Composable
fun MyFabItem(
    item: MiniFabItem,
) {
    ExtendedFloatingActionButton(
        onClick = {
            item.onClick()
        },
        content = {
            Icon(
                imageVector = ImageVector.vectorResource(item.icon),
                contentDescription = null,
                tint = item.tint
            )
            Text(text = item.title)
        }
    )
}