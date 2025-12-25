package com.inkspire.ebookreader.ui.home.libary.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.inkspire.ebookreader.R
import com.inkspire.ebookreader.common.DeviceConfiguration
import com.inkspire.ebookreader.domain.model.MiniFabItem

@Composable
fun MyExpandableFab(
    items: List<MiniFabItem>,
    expanded: Boolean,
    deviceConfiguration: DeviceConfiguration,
    isListEmpty: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            onDismiss()
                        }
                    )
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }) + expandVertically(),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }) + shrinkVertically()
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FlowRow(
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp,
                                start = 16.dp + WindowInsets.safeContent
                                    .only(WindowInsetsSides.Start)
                                    .asPaddingValues()
                                    .calculateStartPadding(LayoutDirection.Rtl)
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items.forEach { fabItems ->
                            key(fabItems.title) {
                                MyFabItem(item = fabItems)
                            }
                        }
                    }
                }
            }

            val transition = updateTransition(targetState = expanded, label = "fab_transition")
            val rotation by transition.animateFloat(label = "fab_rotation") {
                if (it) 315f else 0f
            }
            val alpha by transition.animateFloat(label = "alpha") {
                if (it) 1f else 0.5f
            }
            FloatingActionButton(
                onClick = onToggle,
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                modifier = Modifier
                    .padding(
                        top = 4.dp,
                        end = 16.dp + WindowInsets.safeContent
                            .only(WindowInsetsSides.End)
                            .asPaddingValues()
                            .calculateEndPadding(LayoutDirection.Ltr),
                        bottom = 16.dp + if (deviceConfiguration == DeviceConfiguration.PHONE_LANDSCAPE
                            || deviceConfiguration == DeviceConfiguration.TABLET_LANDSCAPE)
                            WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding()
                        else
                            0.dp

                    )
                    .then(
                        if (isListEmpty) Modifier.alpha(1f) else Modifier.alpha(alpha)
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_add_music),
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}