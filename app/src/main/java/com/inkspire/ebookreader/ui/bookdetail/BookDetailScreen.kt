package com.inkspire.ebookreader.ui.bookdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation3.runtime.NavKey
import coil.compose.AsyncImage
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.ContentPattern
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.navigation.Route
import com.inkspire.ebookreader.ui.composable.MyBookChip
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(
    ExperimentalHazeMaterialsApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun BookDetailScreen(
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit,
    onBack: () -> Unit,
    onNavigate: (NavKey) -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    when (val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)) {
        DeviceConfiguration.PHONE_PORTRAIT,
        DeviceConfiguration.TABLET_PORTRAIT -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                BookDetailHeader(
                    state = state,
                    deviceConfiguration = deviceConfiguration,
                    onAction = onAction,
                    onNavigate = onNavigate,
                    onBack = onBack
                )
                BookDetailFooter(
                    state = state,
                    deviceConfiguration = deviceConfiguration,
                    onAction = onAction,
                    onNavigate = onNavigate
                )
            }
        }
        DeviceConfiguration.PHONE_LANDSCAPE,
        DeviceConfiguration.TABLET_LANDSCAPE -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                BookDetailHeader(
                    state = state,
                    deviceConfiguration = deviceConfiguration,
                    onAction = onAction,
                    onNavigate = onNavigate,
                    onBack = onBack
                )
                BookDetailFooter(
                    state = state,
                    deviceConfiguration = deviceConfiguration,
                    onAction = onAction,
                    onNavigate = onNavigate
                )
            }
        }
    }
    if (state.isShowCategoryMenu) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onAction(BookDetailAction.ChangeCategoryMenuVisibility) },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "BOOK CATEGORY",
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    )
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.categories.forEach { categoryChip ->
                        MyBookChip(
                            selected = categoryChip.isSelected,
                            color = Color(categoryChip.color),
                            onClick = {
                                onAction(BookDetailAction.ChangeChipState(categoryChip))
                            }
                        ) {
                            Text(text = categoryChip.name)
                        }
                    }
                }
            }
        }
    }
}