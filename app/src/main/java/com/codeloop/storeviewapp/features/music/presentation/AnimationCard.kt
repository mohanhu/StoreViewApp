package com.codeloop.storeviewapp.features.music.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AnimationRotation(
    modifier : Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val infiniteDuration = rememberInfiniteTransition()

    val angle by infiniteDuration.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = modifier.graphicsLayer{
        rotationZ = angle
    }) {
        content()
    }
}

@Composable
fun AnimationTextForward(
    modifier : Modifier,
    content: @Composable () -> Unit
) {

    val infiniteDuration = rememberInfiniteTransition()

    val textOffset by infiniteDuration.animateFloat(
        initialValue = 300f,
        targetValue = -600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = modifier.graphicsLayer {
        translationX = textOffset
    }) {
        content()
    }
}