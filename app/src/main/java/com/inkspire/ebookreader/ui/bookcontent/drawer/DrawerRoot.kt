package com.inkspire.ebookreader.ui.bookcontent.drawer

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.domain.model.Book
import com.inkspire.ebookreader.ui.bookcontent.common.LocalDrawerViewModel
import com.inkspire.ebookreader.ui.bookcontent.common.LocalStylingViewModel
import com.inkspire.ebookreader.ui.bookcontent.drawer.bookmark.BookmarkList
import com.inkspire.ebookreader.ui.bookcontent.drawer.note.NoteList
import com.inkspire.ebookreader.ui.bookcontent.drawer.tableofcontent.TableOfContentScreen

@Composable
fun DrawerRoot(
    bookInfoProvider: () -> Book,
) {
    val drawerVM = LocalDrawerViewModel.current
    val stylingVM = LocalStylingViewModel.current

    val drawerState by drawerVM.state.collectAsStateWithLifecycle()
    val stylingState by stylingVM.state.collectAsStateWithLifecycle()

    val coverImagePath by remember {
        derivedStateOf {
            if (bookInfoProvider().coverImagePath == "error") {
                R.drawable.book_cover_not_available
            } else {
                bookInfoProvider().coverImagePath
            }
        }
    }
    val title by remember { derivedStateOf { bookInfoProvider().title } }
    val author by remember { derivedStateOf { bookInfoProvider().authors.joinToString(",") } }
    val selectedTabIndex by remember { derivedStateOf { drawerState.selectedTabIndex } }
    val drawerVisibility by remember { derivedStateOf { drawerState.visibility } }

    LaunchedEffect(drawerVisibility) {
        drawerVM.onAction(DrawerAction.ChangeTabIndex(0))
    }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val imageHeight = when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT -> 140.dp
        DeviceConfiguration.PHONE_LANDSCAPE -> 80.dp
        DeviceConfiguration.TABLET_PORTRAIT -> 160.dp
        DeviceConfiguration.TABLET_LANDSCAPE -> 140.dp
    }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(
                PaddingValues(
                    start = WindowInsets.safeContent
                        .only(WindowInsetsSides.Start)
                        .asPaddingValues()
                        .calculateStartPadding(LayoutDirection.Ltr),
                    top = WindowInsets.safeContent
                        .only(WindowInsetsSides.Top)
                        .asPaddingValues()
                        .calculateTopPadding()
                )
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            AsyncImage(
                model = coverImagePath,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(imageHeight)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = title,
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = stylingState.stylePreferences.textColor,
                            fontWeight = FontWeight.Medium,
                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = author,
                        style = TextStyle(
                            color = stylingState.stylePreferences.textColor,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = FontWeight.Normal,
                            fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                        ),
                    )
                }
            }
        }
        PrimaryTabRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            selectedTabIndex = selectedTabIndex,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        selectedTabIndex,
                        matchContentSize = true
                    ),
                    width = Dp.Unspecified,
                    color = stylingState.stylePreferences.textColor
                )
            },
            containerColor = Color.Transparent,
            contentColor = stylingState.stylePreferences.textColor,
            divider = { HorizontalDivider(color = stylingState.stylePreferences.backgroundColor) }
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = {
                        drawerVM.onAction(DrawerAction.ChangeTabIndex(index))
                    },
                    modifier = Modifier.weight(1f),
                    text = {
                        Text(
                            text = item.title,
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontFamily = stylingState.fontFamilies[stylingState.stylePreferences.fontFamily],
                            )
                        )
                    },
                )
            }
        }
        Crossfade(targetState = selectedTabIndex) { option ->
            when (option) {
                0 -> {
                    TableOfContentScreen()
                }
                1 -> {
                    NoteList()
                }
                2 -> {
                    BookmarkList(
                        bookInfoProvider = bookInfoProvider,
                    )
                }
            }
        }
    }
}