package com.learnit.app.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 110.dp,
    showShadow: Boolean = true,
    grayscale: Boolean = false
) {
    val gradientColors = if (grayscale) {
        listOf(Color.LightGray, Color.LightGray)
    } else {
        listOf(Color(0xFF7B78FF), Color(0xFF5E5CE6))
    }

    val bindingColors = if (grayscale) {
        listOf(Color.LightGray.copy(alpha = 0.8f), Color.LightGray.copy(alpha = 0.8f))
    } else {
        listOf(Color(0xFF908DFF), Color(0xFF6C69FF))
    }

    val shadowColor = if (grayscale) Color.Transparent else Color(0xFF5E5CE6).copy(alpha = 0.25f)

    Box(
        modifier = modifier.size(logoSize * 1.2f),
        contentAlignment = Alignment.Center
    ) {
        if (showShadow && !grayscale) {
            Box(
                modifier = Modifier
                    .size(logoSize * 0.9f, logoSize)
                    .offset(y = (logoSize.value * 0.1f).dp)
                    .blur((logoSize.value * 0.15f).dp)
                    .background(shadowColor, RoundedCornerShape((logoSize.value * 0.2f).dp))
            )
        }

        Canvas(modifier = Modifier.size(logoSize)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val cornerRadiusPx = logoSize.toPx() * 0.18f
            
            // Cover Buku
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(canvasWidth, canvasHeight)
                ),
                size = Size(canvasWidth, canvasHeight * 0.9f),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )

            // Binding (Jilidan)
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = bindingColors,
                    start = Offset(0f, canvasHeight * 0.85f),
                    end = Offset(canvasWidth, canvasHeight)
                ),
                topLeft = Offset(0f, canvasHeight * 0.85f),
                size = Size(canvasWidth, canvasHeight * 0.15f),
                cornerRadius = CornerRadius(cornerRadiusPx / 2f, cornerRadiusPx / 2f)
            )

            // Garis putih
            val lineWidth = canvasWidth * 0.55f
            val lineHeight = logoSize.toPx() * 0.1f
            val startX = (canvasWidth - lineWidth) / 2f
            
            drawRoundRect(
                color = if (grayscale) Color.White.copy(alpha = 0.9f) else Color.White,
                topLeft = Offset(startX, canvasHeight * 0.25f),
                size = Size(lineWidth, lineHeight),
                cornerRadius = CornerRadius(lineHeight / 2f, lineHeight / 2f)
            )

            drawRoundRect(
                color = if (grayscale) Color.White.copy(alpha = 0.9f) else Color.White,
                topLeft = Offset(startX, canvasHeight * 0.45f),
                size = Size(lineWidth * 0.7f, lineHeight),
                cornerRadius = CornerRadius(lineHeight / 2f, lineHeight / 2f)
            )
        }
    }
}
