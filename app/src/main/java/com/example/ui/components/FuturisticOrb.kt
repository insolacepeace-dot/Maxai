package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.AssistantState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Blue600
import com.example.ui.theme.CorePulseViolet
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlowAmber
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.VoidBlack
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FuturisticOrb(
    state: AssistantState,
    audioAmplitude: Float,
    modifier: Modifier = Modifier,
    size: Dp = 230.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_rotation")

    // Fast 3s rotation for spinning tech ring
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_ring"
    )

    // Slower counter rotation for subtle orbital elements
    val counterAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_spin"
    )

    // Pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == AssistantState.LISTENING) 700 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val primaryColor = when (state) {
        AssistantState.IDLE -> Cyan500
        AssistantState.LISTENING -> HologramTeal
        AssistantState.THINKING -> CorePulseViolet
        AssistantState.SPEAKING -> Cyan400
    }

    val secondaryColor = when (state) {
        AssistantState.IDLE -> Blue600
        AssistantState.LISTENING -> GlowAmber
        AssistantState.THINKING -> AlertRed
        AssistantState.SPEAKING -> Cyan500
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = (this.size.minDimension / 2f) * 0.92f
            val dynamicScale = pulseScale + (audioAmplitude * 0.3f)
            val currentRadius = baseRadius * dynamicScale

            // 1. Large Ambient Blur Glow (cyan-500/10 + blue-600/10)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f * glowAlpha),
                        secondaryColor.copy(alpha = 0.18f * glowAlpha),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentRadius * 1.6f
                ),
                radius = currentRadius * 1.6f,
                center = center
            )

            // 2. Outermost Pulse Ring (border-cyan-500/30)
            drawCircle(
                color = primaryColor.copy(alpha = 0.35f * glowAlpha),
                radius = currentRadius * 0.98f,
                center = center,
                style = Stroke(width = 1.5f)
            )

            // 3. Middle Concentric Ring (border-cyan-400/20)
            drawCircle(
                color = primaryColor.copy(alpha = 0.25f),
                radius = currentRadius * 0.85f,
                center = center,
                style = Stroke(width = 1.2f)
            )

            // 4. Rotating Tech Ring with Transparent Segment Gap (border-2 border-cyan-500/40 border-t-transparent animate-spin)
            rotate(spinAngle, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.7f),
                            primaryColor.copy(alpha = 0.1f),
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.8f)
                        ),
                        center = center
                    ),
                    startAngle = 45f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(center.x - currentRadius * 0.72f, center.y - currentRadius * 0.72f),
                    size = Size(currentRadius * 1.44f, currentRadius * 1.44f),
                    style = Stroke(
                        width = 2.5f,
                        cap = StrokeCap.Round
                    )
                )

                // High-tech satellite node dots
                for (i in 0 until 3) {
                    val angleRad = Math.toRadians((i * 120.0))
                    val markerX = center.x + (currentRadius * 0.72f * cos(angleRad)).toFloat()
                    val markerY = center.y + (currentRadius * 0.72f * sin(angleRad)).toFloat()
                    drawCircle(
                        color = primaryColor,
                        radius = 2.5f,
                        center = Offset(markerX, markerY)
                    )
                }
            }

            // 5. Counter-rotating fine dashed orbital telemetry ring
            rotate(counterAngle, pivot = center) {
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.3f),
                    radius = currentRadius * 0.62f,
                    center = center,
                    style = Stroke(
                        width = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f, 4f, 16f), 0f)
                    )
                )
            }

            // 6. Glowing Radial Reactor Core (gradient from cyan-600 to blue-400 with intense shadow aura)
            val coreRadius = currentRadius * 0.52f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.9f),
                        secondaryColor.copy(alpha = 0.7f),
                        primaryColor.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreRadius * 1.25f
                ),
                radius = coreRadius * 1.25f,
                center = center
            )

            // Linear Gradient Core Orb Body
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        primaryColor,
                        secondaryColor
                    ),
                    start = Offset(center.x - coreRadius, center.y - coreRadius),
                    end = Offset(center.x + coreRadius, center.y + coreRadius)
                ),
                radius = coreRadius,
                center = center
            )

            // 7. Inner Pitch-Dark Iris Circle (#050505) with internal radial highlight
            val irisRadius = coreRadius * 0.84f
            drawCircle(
                color = VoidBlack,
                radius = irisRadius,
                center = center
            )

            // Radial highlight inside dark iris (from-cyan-500/20 via-transparent)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f * (0.8f + audioAmplitude)),
                        primaryColor.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = irisRadius
                ),
                radius = irisRadius,
                center = center
            )

            // 8. Concentric Central Reactor Symbol / Concentric Rings & Core Pupil
            drawCircle(
                color = primaryColor.copy(alpha = 0.7f),
                radius = irisRadius * 0.65f,
                center = center,
                style = Stroke(width = 1.5f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryColor,
                        Color.Transparent
                    ),
                    center = center,
                    radius = irisRadius * 0.38f * (1f + audioAmplitude * 0.5f)
                ),
                radius = irisRadius * 0.38f * (1f + audioAmplitude * 0.5f),
                center = center
            )
        }
    }
}

