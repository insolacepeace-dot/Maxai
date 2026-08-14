package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.NeonCyan
import kotlin.random.Random

private data class StarParticle(
    val xRel: Float,
    val yRel: Float,
    val radius: Float,
    val alpha: Float,
    val speed: Float
)

@Composable
fun ParticleBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 36
) {
    val particles = remember {
        val random = Random(42)
        List(particleCount) {
            StarParticle(
                xRel = random.nextFloat(),
                yRel = random.nextFloat(),
                radius = random.nextFloat() * 1.8f + 0.6f,
                alpha = random.nextFloat() * 0.4f + 0.15f,
                speed = random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            val curY = ((p.yRel + (drift * p.speed)) % 1f) * h
            val curX = p.xRel * w

            drawCircle(
                color = if (p.speed > 0.75f) NeonCyan.copy(alpha = p.alpha) else DeepSapphire.copy(alpha = p.alpha),
                radius = p.radius,
                center = Offset(curX, curY)
            )
        }
    }
}
