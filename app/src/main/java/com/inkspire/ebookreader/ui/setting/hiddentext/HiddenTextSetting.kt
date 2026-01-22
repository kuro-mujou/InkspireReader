package com.inkspire.ebookreader.ui.setting.hiddentext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HiddenTextSetting(
    stylingState: StylingState?
) {
    val viewModel = koinViewModel<HiddenTextViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter Texts",
                style = MaterialTheme.typography.titleMedium,
                color = stylingState?.stylePreferences?.textColor ?: MaterialTheme.colorScheme.onSurface,
                fontFamily = stylingState?.fontFamilies[stylingState.stylePreferences.fontFamily]
            )

            Button(
                onClick = {
                    viewModel.onAction(HiddenTextAction.OnDeleteHiddenTexts)
                },
                enabled = state.hiddenTexts.any { it.isSelected },
                colors = ButtonDefaults.buttonColors(
                    containerColor = stylingState?.stylePreferences?.textColor ?: Color.Red.copy(alpha = 0.8f),
                    disabledContainerColor = stylingState?.containerColor?.copy(alpha = 0.3f) ?: Color.Gray.copy(alpha = 0.3f),
                    contentColor = stylingState?.containerColor ?: Color.White,
                    disabledContentColor = stylingState?.stylePreferences?.textColor?.copy(alpha = 0.3f) ?: Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp).width(16.dp)
                )
                Text("Delete (${state.hiddenTexts.filter { it.isSelected }.size})")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.hiddenTexts.isEmpty()) {
            Text(
                text = "No hidden texts found.",
                color = stylingState?.stylePreferences?.textColor?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontFamily = stylingState?.fontFamilies[stylingState.stylePreferences.fontFamily],
                modifier = Modifier.padding(vertical = 24.dp).align(Alignment.CenterHorizontally)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.hiddenTexts.forEach { item ->
                        FilterChip(
                            selected = item.isSelected,
                            onClick = {
                                viewModel.onAction(HiddenTextAction.ToggleHiddenTextSelectedState(item))
                            },
                            label = {
                                Text(
                                    text = item.textToHide,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (item.isSelected)
                                        stylingState?.stylePreferences?.backgroundColor ?: MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        stylingState?.stylePreferences?.textColor ?: MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingIcon = {
                                if (item.isSelected) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_confirm),
                                        contentDescription = null,
                                        modifier = Modifier.width(16.dp),
                                        tint = stylingState?.stylePreferences?.backgroundColor ?: MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = stylingState?.containerColor ?: MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = stylingState?.stylePreferences?.textColor ?: MaterialTheme.colorScheme.onSurface,
                                selectedContainerColor = stylingState?.stylePreferences?.textColor ?: MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = stylingState?.stylePreferences?.backgroundColor ?: MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}