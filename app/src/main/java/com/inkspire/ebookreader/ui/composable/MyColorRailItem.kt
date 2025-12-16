package com.inkspire.ebookreader.ui.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.util.ColorUtil.darken


@Composable
fun MyColorRailItem(
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.padding(vertical = 8.dp),
        color = color,
        shape = CircleShape,
        selected = selected,
        onClick = onClick,
        border = BorderStroke(
            width = 2.dp,
            color = if (selected)
                color.darken(0.3f)
            else
                Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ){
            if(selected){
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_confirm),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}