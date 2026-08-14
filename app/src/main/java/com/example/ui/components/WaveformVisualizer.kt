package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.AssistantState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CorePulseViolet
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.HologramTeal
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    state: AssistantState,
    amplitude: Float,
    modifier: Modifier = Modifier,
    height: Dp = 32.dp,
    barCount: Int = 24
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val primaryBarColor = when (state) {
        AssistantState.LISTENING -> HologramTeal
        AssistantState.THINKING -> CorePulseViolet
        AssistantState.SPEAKING -> Cyan400
        AssistantState.IDLE -> Cyan500
    }

    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val totalWidth = size.width
        val canvasHeight = size.height
        val barWidth = 4f
        val spacing = (totalWidth - (barWidth * barCount)) / (barCount - 1)

        val isActive = state != AssistantState.IDLE
        val ampMultiplier = if (isActive) (0.35f + amplitude * 0.65f) else 0.15f

        for (i in 0 until barCount) {
            val progress = i.toFloat() / barCount.toFloat()
            // Bell curve dampening at edges
            val envelope = sin(progress * Math.PI).toFloat()

            val wave1 = sin((progress * 4 * Math.PI + phase).toDouble()).toFloat()
            val wave2 = sin((progress * 7 * Math.PI - phase * 1.4).toDouble()).toFloat()
            val combinedWave = ((wave1 + wave2) / 2f).coerceIn(-1f, 1f)

            val barHeight = ((canvasHeight * 0.18f) + (canvasHeight * 0.78f * envelope * ampMultiplier * (0.6f + 0.4f * combinedWave))).coerceIn(4f, canvasHeight)

            val x = i * (barWidth + spacing)
            val y = (canvasHeight - barHeight) / 2f

            val alphaVal = if (i % 3 == 0) 1.0f else if (i % 2 == 0) 0.65f else 0.4f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryBarColor.copy(alpha = alphaVal),
                        primaryBarColor.copy(alpha = alphaVal * 0.6f)
                    ),
                    startY = y,
                    endY = y + barHeight
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

