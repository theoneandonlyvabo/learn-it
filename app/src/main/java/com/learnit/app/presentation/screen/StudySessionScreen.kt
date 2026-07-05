package com.learnit.app.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnit.app.domain.model.SessionUiState

@Composable
fun StudySessionScreen(
    state: SessionUiState = SessionUiState(),
    onBackClick: () -> Unit = {},
    onFlip: () -> Unit = {},
    onNext: () -> Unit = {},
    onFinish: () -> Unit = {}
) {
    val currentCard = state.currentCard

    val rotation by animateFloatAsState(
        targetValue = if (state.isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "CardFlip"
    )

    Scaffold(
        topBar = {
            StudyTopBar(onBackClick = onBackClick, timeRemaining = state.timeRemaining)
        }
    ) { paddingValues ->
        if (currentCard != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFBFBFF))
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentCard.topic.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 3. Tampilkan kartu berdasarkan data saat ini
                FlashcardCard(
                    rotation = rotation,
                    onFlip = onFlip,
                    question = currentCard.question,
                    answer = currentCard.answer
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 4. Tombol Navigasi Dinamis
                // ponytail: Previous disabled — StudySessionViewModel only exposes nextCard(),
                // since score is revealed-cards-based and not designed to be undone. Add
                // previousCard() to the VM if going back becomes a requirement.
                NavigationButtons(
                    onPrevious = {},
                    onNext = { if (state.isLastCard) onFinish() else onNext() },
                    isPreviousEnabled = false,
                    isNextEnabled = true, // Always enabled, becomes "Submit" at the end
                    nextButtonText = if (state.isLastCard) "Submit" else "Next"
                )

                Spacer(modifier = Modifier.weight(1f))

                // 5. Progress Section Otomatis sesuai totalCards
                ProgressSection(current = state.currentCardIndex + 1, total = state.cards.size)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTopBar(onBackClick: () -> Unit, timeRemaining: Int = 0) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Learn-it",
                    color = Color(0xFF323499),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
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
            Surface(
                color = Color(0xFFE8E8FF),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color(0xFF5E5CE6),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatTime(timeRemaining),
                        color = Color(0xFF5E5CE6),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun FlashcardCard(
    rotation: Float,
    onFlip: () -> Unit,
    question: String,
    answer: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val tapScale by animateFloatAsState(if (isPressed) 1.2f else 1f, label = "TapScale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null, 
                onClick = onFlip
            )
            .shadow(
                elevation = 24.dp, 
                shape = RoundedCornerShape(32.dp), 
                spotColor = Color(0xFF5E5CE6).copy(alpha = 0.25f),
                ambientColor = Color(0xFF5E5CE6).copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFF3E8FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color(0xFF9D5CE6),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    Text(
                        text = question,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF323499),
                        lineHeight = 36.sp
                    )
                    
                    Spacer(modifier = Modifier.height(60.dp))
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.scale(tapScale)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = if (isPressed) Color(0xFF5E5CE6) else Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Tap to flip",
                            fontSize = 12.sp,
                            fontWeight = if (isPressed) FontWeight.Bold else FontWeight.Normal,
                            color = if (isPressed) Color(0xFF5E5CE6) else Color.LightGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(32.dp)
                        .graphicsLayer { rotationY = 180f }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE8E8FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF5E5CE6),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    Text(
                        text = buildAnnotatedString {
                            append(answer)
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color.Black.copy(alpha = 0.7f),
                        lineHeight = 28.sp
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Answer Side",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationButtons(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isPreviousEnabled: Boolean,
    isNextEnabled: Boolean,
    nextButtonText: String = "Next"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onPrevious,
            enabled = isPreviousEnabled,
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE8E8F5),
                disabledContainerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = if (isPreviousEnabled) Color.Gray else Color.LightGray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Previous", color = if (isPreviousEnabled) Color.Gray else Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Button(
            onClick = onNext,
            enabled = isNextEnabled,
            modifier = Modifier
                .weight(1.2f)
                .height(60.dp)
                .shadow(
                    elevation = if (isNextEnabled) 12.dp else 0.dp, 
                    shape = RoundedCornerShape(18.dp), 
                    spotColor = Color(0xFF5E5CE6).copy(alpha = 0.5f)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5E5CE6),
                disabledContainerColor = Color(0xFFE0E0E0)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(nextButtonText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (nextButtonText == "Next") {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun ProgressSection(current: Int, total: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Progress", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(text = "Card $current of $total", fontSize = 13.sp, color = Color(0xFF323499), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { current.toFloat() / total.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = Color(0xFF5E5CE6),
            trackColor = Color(0xFFE8E8F5)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StudySessionPreview() {
    StudySessionScreen(
        state = SessionUiState(
            cards = listOf(
                com.learnit.app.domain.model.Flashcard(
                    id = 1,
                    question = "What is the powerhouse of the cell?",
                    answer = "The mitochondria.",
                    topic = "Biology: Cell Structures"
                )
            ),
            timeRemaining = 298
        )
    )
}
