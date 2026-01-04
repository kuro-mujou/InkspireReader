package com.inkspire.ebookreader.ui.bookcontent.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun CustomFab(
    stylingState: StylingState,
    onFabClick: () -> Unit,
) {
    FilledIconButton(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .size(48.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = stylingState.stylePreferences.textColor
        ),
        shape = RoundedCornerShape(10.dp),
        onClick = {
            onFabClick()
        }
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_undo),
            contentDescription = null,
            tint = stylingState.stylePreferences.backgroundColor
        )
    }
}