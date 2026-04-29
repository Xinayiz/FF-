package com.ff.app.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.sin

@Composable
fun WaveBackground(
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    amplitude: Float = 50f,
    color: Color = Color(0xFF0066FF).copy(alpha = 0.1f)
) {
    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 / (speed / 0.5f).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val wavePath = Path()
        wavePath.moveTo(0f, height / 2)
        for (x in 0..width.toInt() step 5) {
            val y = height / 2 + amplitude * sin((x + offset) * 0.02f)
            wavePath.lineTo(x.toFloat(), y)
        }
        wavePath.lineTo(width, height)
        wavePath.lineTo(0f, height)
        wavePath.close()
        drawPath(wavePath, color)
    }
}
