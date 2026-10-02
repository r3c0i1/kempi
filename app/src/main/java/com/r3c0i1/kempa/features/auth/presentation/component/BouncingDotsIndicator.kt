package com.r3c0i1.kempa.features.auth.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun BouncingDotsIndicator(
    modifier: Modifier = Modifier,
    dotSize: Dp = 7.dp,
    dotColor: Color = LocalContentColor.current,
    travelDistance: Dp = 6.dp
) {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay((index * 120L).milliseconds)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1000
                        0.0f at 0 using FastOutSlowInEasing
                        1.0f at 300 using FastOutSlowInEasing
                        0.0f at 600 using FastOutSlowInEasing
                        0.0f at 1000
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEach { animatable ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        translationY = -animatable.value * travelDistance.toPx()
                        alpha = 0.4f + (animatable.value * 0.6f)
                    }
                    .background(color = dotColor, shape = CircleShape)
            )
        }
    }
}
