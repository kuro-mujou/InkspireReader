package com.inkspire.ebookreader.ui.bookdetail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.ui.composable.MyBookChip
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

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