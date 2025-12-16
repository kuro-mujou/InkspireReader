package com.inkspire.ebookreader.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.common.BookmarkStyle
import com.inkspire.ebookreader.domain.model.BookmarkMenuItem
import com.inkspire.ebookreader.domain.model.SettingState

@Composable
fun MyBookmarkItemView(
    settingState: SettingState,
    listItem: BookmarkMenuItem,
    onSelected: (BookmarkStyle) -> Unit
) {
    val checked = settingState.selectedBookmarkStyle == listItem.bookmarkStyle
    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                onSelected(listItem.bookmarkStyle)
            },
        )
        BookmarkCard(
            bookmarkContent = listItem.title,
            bookmarkIndex = listItem.id,
            bookmarkStyle = listItem.bookmarkStyle,
            onCardClicked = {
                onSelected(listItem.bookmarkStyle)
            },
        )
    }
}