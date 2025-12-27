package com.inkspire.ebookreader.ui.setting.bookmark.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.domain.model.BookmarkMenuItem
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import com.inkspire.ebookreader.ui.setting.bookmark.BookmarkSettingState

@Composable
fun MyBookmarkItemView(
    state: BookmarkSettingState,
    listItem: BookmarkMenuItem,
    stylingState: StylingState?,
    onSelected: () -> Unit,
) {
    val checked = state.selectedBookmarkStyle == listItem.bookmarkStyle
    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                onSelected()
            },
            colors = CheckboxDefaults.colors(
                checkedColor = stylingState?.textColor ?: MaterialTheme.colorScheme.primary,
                uncheckedColor = stylingState?.textColor ?: MaterialTheme.colorScheme.primary,
                checkmarkColor = stylingState?.backgroundColor ?: MaterialTheme.colorScheme.background
            )
        )
        MyBookmarkCard(
            bookmarkContent = listItem.title,
            bookmarkIndex = listItem.id,
            bookmarkStyle = listItem.bookmarkStyle,
            deletable = false,
            stylingState = stylingState,
            onCardClicked = onSelected,
            onDeleted = {}
        )
    }
}