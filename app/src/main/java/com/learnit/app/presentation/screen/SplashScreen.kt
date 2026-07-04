package com.learnit.app.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigate: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Animasi fade in untuk konten agar muncul secara elegan
    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 400),
        label = "ContentAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500) 
        onNavigate()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFBFBFF) 
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BackgroundElements()

            Column(
                modifier = Modifier
                    .graphicsLayer { alpha = contentAlpha }
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Area Logo (dikosongkan karena logo dihandle oleh MainActivity untuk transisi)
                Box(modifier = Modifier.size(80.dp))

                Spacer(modifier = Modifier.height(0.dp))

                // Bagian Identitas Aplikasi
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Learn-it",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF323499),
                        letterSpacing = (-1.0).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Learn Smarter with AI",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B6EBF).copy(alpha = 0.85f),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BackgroundElements() {
    Box(modifier = Modifier.fillMaxSize()) {
        val dotColor = Color(0xFFC0C0E6).copy(alpha = 0.4f)
        Box(modifier = Modifier.offset(40.dp, 100.dp).size(6.dp).background(dotColor, RoundedCornerShape(3.dp)))
        Box(modifier = Modifier.offset(300.dp, 200.dp).size(10.dp).background(dotColor, RoundedCornerShape(5.dp)))
        Box(modifier = Modifier.offset(80.dp, 450.dp).size(8.dp).background(dotColor, RoundedCornerShape(4.dp)))
        Box(modifier = Modifier.offset(280.dp, 600.dp).size(5.dp).background(dotColor, RoundedCornerShape(2.5.dp)))
        Box(modifier = Modifier.offset(150.dp, 750.dp).size(12.dp).background(dotColor, RoundedCornerShape(6.dp)))
    }
}
