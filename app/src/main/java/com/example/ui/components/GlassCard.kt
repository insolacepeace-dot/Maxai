package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderActive
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceVariant
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.GlassWhite5

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderGlow: Boolean = false,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val borderBrush = if (borderGlow) {
        Brush.linearGradient(
            colors = listOf(
                GlassBorderActive,
                GlassWhite10,
                GlassBorderActive.copy(alpha = 0.4f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                GlassWhite10,
                Color(0x08FFFFFF)
            )
        )
    }

    Surface(
        modifier = modifier.clip(shape),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderBrush)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x1AFFFFFF), // top subtle glass highlight
                            Color(0x0AFFFFFF), // body
                            Color(0x04FFFFFF)
                        )
                    )
                )
                .padding(contentPadding),
            content = content
        )
    }
}

