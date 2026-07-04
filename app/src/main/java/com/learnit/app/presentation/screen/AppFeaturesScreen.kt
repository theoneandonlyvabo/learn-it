package com.learnit.app.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FeatureStep(
    val step: String,
    val title: String,
    val description: String,
    val stepColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFeaturesScreen(
    onBackClick: () -> Unit = {},
    onStartCreatingClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showInfoDialog by remember { mutableStateOf(false) }

    val featureSteps = remember {
        listOf(
            FeatureStep(
                step = "STEP 01",
                title = "Input your Content",
                description = "Paste notes, articles, or full textbooks. Our system intelligently processes various formats, stripping away the fluff to find the core insights.",
                stepColor = Color(0xFF5E5CE6)
            ),
            FeatureStep(
                step = "STEP 02",
                title = "AI Core Analysis",
                description = "Our proprietary engine maps out semantic links, identifies critical definitions, and predicts potential exam questions using deep contextual understanding.",
                stepColor = Color(0xFF5E5CE6)
            ),
            FeatureStep(
                step = "STEP 03",
                title = "Magic Cards Created",
                description = "Voila! High-impact flashcards are generated instantly. Review them through our spaced-repetition algorithm to lock knowledge in forever.",
                stepColor = Color(0xFF5E5CE6)
            )
        )
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("About Learn-it", fontWeight = FontWeight.Bold) },
            text = { Text("Learn-it uses AI to help students generate flashcards and optimize their study process.") },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppFeaturesTopBar(
                onBackClick = onBackClick,
                onInfoClick = { showInfoDialog = true }
            )
        },
        containerColor = Color(0xFFFBFBFF)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HeroSection()
                Spacer(modifier = Modifier.height(48.dp))
            }

            items(featureSteps) { step ->
                FeatureStepCard(step)
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
                CallToActionCard(
                    onStartCreatingClick = {
                        onStartCreatingClick()
                        Toast.makeText(context, "Navigate to Flashcard Generator", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFeaturesTopBar(onBackClick: () -> Unit, onInfoClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "App Features",
                color = Color(0xFF323499),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF323499)
                )
            }
        },
        actions = {
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = Color(0xFF323499)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun HeroSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Turn Noise into\nKnowledge",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF323499),
            textAlign = TextAlign.Center,
            lineHeight = 44.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Experience the magic of instant learning. Our AI ecosystem transforms your raw study material into retention-optimized flashcards in seconds.",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun FeatureStepCard(step: FeatureStep) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color.LightGray.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(32.dp)) {
            Text(
                text = step.step,
                color = step.stepColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = step.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = step.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun CallToActionCard(onStartCreatingClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(40.dp),
                spotColor = Color(0xFF5E5CE6).copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(40.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF5E5CE6), Color(0xFF323499))
                    )
                )
                .padding(32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ready to elevate your learning?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Join thousands of students using Learn-it to master their subjects and accelerate their learning journey.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onStartCreatingClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Start Creating Now",
                        color = Color(0xFF323499),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppFeaturesPreview() {
    AppFeaturesScreen()
}
