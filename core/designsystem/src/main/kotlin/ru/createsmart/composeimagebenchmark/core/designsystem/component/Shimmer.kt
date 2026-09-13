package ru.createsmart.composeimagebenchmark.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import ru.createsmart.composeimagebenchmark.core.designsystem.theme.ShimmerDarkBase
import ru.createsmart.composeimagebenchmark.core.designsystem.theme.ShimmerDarkHighlight
import ru.createsmart.composeimagebenchmark.core.designsystem.theme.ShimmerLightBase
import ru.createsmart.composeimagebenchmark.core.designsystem.theme.ShimmerLightHighlight

/**
 * High-performance Shimmer modifier.
 *
 * Optimization: Animation reading happens in drawBehind (Draw Phase).
 * This prevents recomposition and layout passes during animation.
 */
@Composable
public fun Modifier.shimmerEffect(
    isDark: Boolean = isSystemInDarkTheme(),
): Modifier {
    val baseColor = if (isDark) ShimmerDarkBase else ShimmerLightBase
    val highlightColor = if (isDark) ShimmerDarkHighlight else ShimmerLightHighlight

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ShimmerTranslate",
    )

    return this.drawBehind {
        val width = size.width
        val height = size.height
        val totalDistance = width + height
        val currentOffset = totalDistance * translateAnim

        val brush = Brush.linearGradient(
            colors = listOf(
                baseColor,
                highlightColor,
                baseColor,
            ),
            start = Offset(x = currentOffset - width, y = currentOffset - height),
            end = Offset(x = currentOffset, y = currentOffset),
        )

        drawRect(brush = brush)
    }
}
