package com.learnit.app.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.learnit.app.R

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 110.dp,
    showShadow: Boolean = true,
    grayscale: Boolean = false
) {
    val shadowColor = if (grayscale) Color.Transparent else Color(0xFF5E5CE6).copy(alpha = 0.25f)
    val colorFilter = if (grayscale) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null

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

        Image(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = "Learn-it logo",
            modifier = Modifier.size(logoSize),
            colorFilter = colorFilter
        )
    }
}
