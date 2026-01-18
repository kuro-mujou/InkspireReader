package com.inkspire.ebookreader.ui.bookcontent.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.bookcontent.styling.getHighlightColors

@Composable
fun SelectionMenu(
    stylingState: StylingState,
    onHighlight: (Int) -> Unit,
    onClearHighlight: () -> Unit,
    onAddNote: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.7f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = stylingState.containerColor)
    ) {
        val textFieldState = rememberTextFieldState()
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }


        Row {
            LazyRow(
                modifier = Modifier.width(120.dp)
            ) {
                itemsIndexed(stylingState.getHighlightColors()){ index, it ->
                    IconButton(onClick = { onHighlight(index) }) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    width = 1.dp,
                                    color = stylingState.stylePreferences.textColor,
                                    shape = CircleShape
                                )
                                .background(it, shape = CircleShape)
                        )
                    }
                }
            }
            IconButton(onClick = onClearHighlight) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_text_highlight_clear),
                    contentDescription = "Highlight Clear",
                    tint = stylingState.stylePreferences.textColor
                )
            }
        }
    }
}