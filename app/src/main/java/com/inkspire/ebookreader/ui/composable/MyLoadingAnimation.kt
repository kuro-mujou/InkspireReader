package com.inkspire.ebookreader.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.zIndex
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.inkspire.ebookreader.ui.bookcontent.styling.StylingState

@Composable
fun MyLoadingAnimation() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("loading_animation.json")
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
    }
}

@Composable
fun MyLoadingAnimation(
    stylingState: StylingState
) {
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                stylingState.textColor.toArgb(),
                BlendModeCompat.SRC_ATOP
            ),
            keyPath = arrayOf("**")
        )
    )
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("loading_animation.json")
    )
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            },
        color = Color.Transparent
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                dynamicProperties = dynamicProperties
            )
            Text(
                text = "Loading...",
                style = TextStyle(
                    color = stylingState.textColor,
                    fontFamily = stylingState.fontFamilies[stylingState.selectedFontFamilyIndex]
                )
            )
        }
    }
}