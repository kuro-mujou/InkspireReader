package com.inkspire.ebookreader.ui.bookdetail

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import coil.compose.AsyncImage
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.navigation.Route
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BookDetailHeader(
    state: BookDetailState,
    deviceConfiguration: DeviceConfiguration,
    onAction: (BookDetailAction) -> Unit,
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    val hazeState = remember { HazeState() }
    val hazeStyleLight = HazeMaterials.ultraThin(Color(0xFFDCE0E5))
    val hazeStyleDark = HazeMaterials.ultraThin(Color(0xFF181C20))
    val baseModifier = when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT,
        DeviceConfiguration.TABLET_PORTRAIT -> {
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        }
        DeviceConfiguration.PHONE_LANDSCAPE,
        DeviceConfiguration.TABLET_LANDSCAPE -> {
            Modifier
                .fillMaxWidth(0.4f)
                .fillMaxHeight()
        }
    }
    val backgroundModifier =  when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT,
        DeviceConfiguration.TABLET_PORTRAIT -> {
            Modifier
                .fillMaxWidth()
                .dropShadow(
                    shape = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        color = Color(0x40000000),
                    )
                )
                .clip(RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp))
        }
        DeviceConfiguration.PHONE_LANDSCAPE,
        DeviceConfiguration.TABLET_LANDSCAPE -> {
            Modifier
                .fillMaxSize()
                .dropShadow(
                    shape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        color = Color(0x40000000),
                    )
                )
                .clip(RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp))
        }
    }
    val contentModifier = when (deviceConfiguration) {
        DeviceConfiguration.PHONE_PORTRAIT,
        DeviceConfiguration.TABLET_PORTRAIT -> {
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    top = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateTopPadding()
                )
                .clip(RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp))
        }
        DeviceConfiguration.PHONE_LANDSCAPE,
        DeviceConfiguration.TABLET_LANDSCAPE -> {
            Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateTopPadding(),
                    start = WindowInsets.systemBars
                        .union(WindowInsets.displayCutout)
                        .asPaddingValues()
                        .calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .clip(RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp))
        }
    }

    Box(
        modifier = baseModifier
    ) {
        Box(
            modifier = backgroundModifier
                .matchParentSize()
        ) {
            AsyncImage(
                model = if (state.bookWithCategories?.book?.coverImagePath == "error")
                    R.drawable.book_cover_not_available
                else
                    state.bookWithCategories?.book?.coverImagePath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Modifier.hazeSource(state = hazeState)
                        } else
                            Modifier
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f))
            )
        }
        Box(
            modifier = contentModifier
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    IconButton(
                        onClick = {
                            onAction(BookDetailAction.OnBookMarkClick)
                        },
                    ) {
                        Icon(
                            imageVector = if (state.bookWithCategories?.book?.isFavorite == true)
                                ImageVector.vectorResource(R.drawable.ic_bookmark_filled)
                            else
                                ImageVector.vectorResource(R.drawable.ic_bookmark),
                            contentDescription = null,
                            tint = if (state.bookWithCategories?.book?.isFavorite == true)
                                Color(155, 212, 161)
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .width(125.dp)
                            .then(
                                when (deviceConfiguration) {
                                    DeviceConfiguration.PHONE_PORTRAIT,
                                    DeviceConfiguration.TABLET_PORTRAIT -> {
                                        Modifier.clip(
                                            RoundedCornerShape(
                                                topStart = 8.dp,
                                                topEnd = 8.dp,
                                                bottomStart = 30.dp,
                                                bottomEnd = 8.dp
                                            )
                                        )
                                    }

                                    DeviceConfiguration.PHONE_LANDSCAPE,
                                    DeviceConfiguration.TABLET_LANDSCAPE -> {
                                        Modifier.clip(
                                            RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = when (deviceConfiguration) {
                                    DeviceConfiguration.PHONE_PORTRAIT,
                                    DeviceConfiguration.TABLET_PORTRAIT -> {
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp,
                                            bottomStart = 30.dp,
                                            bottomEnd = 8.dp
                                        )
                                    }

                                    DeviceConfiguration.PHONE_LANDSCAPE,
                                    DeviceConfiguration.TABLET_LANDSCAPE -> {
                                        RoundedCornerShape(8.dp)
                                    }
                                }
                            )
                    ) {
                        AsyncImage(
                            model = if (state.bookWithCategories?.book?.coverImagePath == "error")
                                R.drawable.book_cover_not_available
                            else
                                state.bookWithCategories?.book?.coverImagePath,
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth()
                            .then(
                                when (deviceConfiguration) {
                                    DeviceConfiguration.PHONE_PORTRAIT,
                                    DeviceConfiguration.TABLET_PORTRAIT -> {
                                        Modifier.clip(
                                            RoundedCornerShape(
                                                topStart = 8.dp,
                                                topEnd = 8.dp,
                                                bottomStart = 8.dp,
                                                bottomEnd = 30.dp
                                            )
                                        )
                                    }

                                    DeviceConfiguration.PHONE_LANDSCAPE,
                                    DeviceConfiguration.TABLET_LANDSCAPE -> {
                                        Modifier.clip(
                                            RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            )
                            .then(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (isSystemInDarkTheme()) {
                                        Modifier
                                            .background(Color.Transparent)
                                            .hazeEffect(
                                                state = hazeState,
                                                style = hazeStyleDark,
                                            )
                                    } else {
                                        Modifier
                                            .background(Color.Transparent)
                                            .hazeEffect(
                                                state = hazeState,
                                                style = hazeStyleLight
                                            )
                                    }
                                } else {
                                    Modifier.background(
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            )
                    ) {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = state.bookWithCategories?.book?.title ?: "",
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    MaterialTheme.colorScheme.onBackground
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontSize = when (deviceConfiguration) {
                                    DeviceConfiguration.PHONE_PORTRAIT -> {
                                        MaterialTheme.typography.headlineSmall.fontSize
                                    }
                                    DeviceConfiguration.TABLET_PORTRAIT -> {
                                        MaterialTheme.typography.headlineSmall.fontSize
                                    }
                                    DeviceConfiguration.PHONE_LANDSCAPE -> {
                                        MaterialTheme.typography.titleSmall.fontSize
                                    }
                                    DeviceConfiguration.TABLET_LANDSCAPE -> {
                                        MaterialTheme.typography.headlineSmall.fontSize
                                    }
                                },
                                fontWeight = FontWeight.Medium
                            ),
                        )
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = state.bookWithCategories?.book?.authors?.joinToString(",")
                                ?: "",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontSize = when (deviceConfiguration) {
                                    DeviceConfiguration.PHONE_PORTRAIT -> {
                                        MaterialTheme.typography.bodyMedium.fontSize
                                    }
                                    DeviceConfiguration.TABLET_PORTRAIT -> {
                                        MaterialTheme.typography.bodyMedium.fontSize
                                    }
                                    DeviceConfiguration.PHONE_LANDSCAPE -> {
                                        MaterialTheme.typography.bodySmall.fontSize
                                    }
                                    DeviceConfiguration.TABLET_LANDSCAPE -> {
                                        MaterialTheme.typography.bodyMedium.fontSize
                                    }
                                }
                            ),
                        )
                    }
                }
                when (deviceConfiguration) {
                    DeviceConfiguration.PHONE_LANDSCAPE -> {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    DeviceConfiguration.TABLET_LANDSCAPE -> {
                        BookDetailExtraInfo(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            state = state,
                            onAction = onAction
                        )
                    }
                    else -> {}
                }
                when (deviceConfiguration) {
                    DeviceConfiguration.PHONE_LANDSCAPE,
                    DeviceConfiguration.TABLET_LANDSCAPE -> {
                        Button(
                            onClick = {
                                onNavigate(Route.BookContent(bookId = state.bookWithCategories?.book?.bookId ?: ""))
                            },
                            modifier = Modifier
                                .then(
                                    if (deviceConfiguration == DeviceConfiguration.PHONE_LANDSCAPE)
                                        Modifier.fillMaxWidth()
                                    else
                                        Modifier.fillMaxWidth(0.8f)
                                )
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Read Book",
                                style = TextStyle(
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}