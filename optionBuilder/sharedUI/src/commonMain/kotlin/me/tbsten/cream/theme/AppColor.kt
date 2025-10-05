package me.tbsten.cream.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

val SeedColor = Color(0xFFFFEAF0)

val MainGradient = Brush.verticalGradient(0f to Color(0xFFEC4899), 1f to Color(0xFFF9A8D4))
val SubGradient = Brush.horizontalGradient(0f to Color(0xFF8B5CF6), 1f to Color(0xFFC4B5FD))

@Composable
fun backgroundGradient(): Brush {
    val gradientOffset = remember { Animatable(0.75f, visibilityThreshold = 0.05f) }
    LaunchedEffect(Unit) {
        val delayMillisPer1Turn = 5.seconds.inWholeMilliseconds
        println("${gradientOffset.value} to 0.00")
        gradientOffset.animateTo(
            targetValue = 0.05f,
            animationSpec = tween(durationMillis = (delayMillisPer1Turn * abs(0.05f - gradientOffset.value)).toInt()),
        )
        while (true) {
            println("${gradientOffset.value} to 1.00")
            gradientOffset.animateTo(
                targetValue = 0.95f,
                animationSpec = tween(durationMillis = (delayMillisPer1Turn * 1.5 * abs(0.95f - gradientOffset.value)).toInt()),
            )
            println("${gradientOffset.value} to 0.00")
            gradientOffset.animateTo(
                targetValue = 0.05f,
                animationSpec = tween(durationMillis = (delayMillisPer1Turn * abs(0.05f - gradientOffset.value)).toInt()),
            )
        }
    }

    return Brush.linearGradient(
        0f to Color(0xFFF8E8F5),
        gradientOffset.value to Color(0xFFF0DFF4),
        1f to Color(0xFFE8D5F2),
    )
}
