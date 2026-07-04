package com.learnit.app.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SessionResult(
    val correctAnswers: Int,
    val totalCards: Int,
    val timeSpent: String,
    val streakDays: Int,
    val level: Int,
    val currentXp: Int,
    val maxXp: Int,
    val gainedXp: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionResultScreen(
    result: SessionResult = SessionResult(23, 25, "4m 12s", 8, 12, 1250, 1800, 450),
    onBackToDecks: () -> Unit = {},
    onViewLeaderboard: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {}
) {
    Scaffold(
        topBar = { SessionTopBar(onBackClick = onBackToDecks) },
        bottomBar = {
            ResultBottomNavigationBar(
                onHomeClick = onHomeClick,
                onFlashcardsClick = onFlashcardsClick,
                onStudyClick = { /* Already in Study flow */ },
                onLeaderboardClick = onViewLeaderboard
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBFBFF))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Trophy
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFE8E8FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFF5E5CE6),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Session Complete!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            
            Text(
                text = "You're crushing your goals today.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Accuracy Card
            AccuracyCard(correctAnswers = result.correctAnswers, totalCards = result.totalCards)

            Spacer(modifier = Modifier.height(24.dp))

            // Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ResultStatsCard(
                    label = "Cards Mastered",
                    value = "${result.correctAnswers}/${result.totalCards}",
                    icon = Icons.Default.Style,
                    modifier = Modifier.weight(1f)
                )
                ResultStatsCard(
                    label = "Time Spent",
                    value = result.timeSpent,
                    icon = Icons.Default.Timer,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Streak Card
            StreakCard(streakDays = result.streakDays)

            Spacer(modifier = Modifier.height(16.dp))

            // Score Uploaded Card
            ScoreUploadedCard(onClick = onViewLeaderboard)

            Spacer(modifier = Modifier.height(32.dp))

            // XP Progress Section
            XpProgressSection(
                level = result.level,
                currentXp = result.currentXp,
                maxXp = result.maxXp,
                gainedXp = result.gainedXp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            Button(
                onClick = onBackToDecks,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.5f)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E5CE6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Back to Decks", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onViewLeaderboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("View Leaderboard", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Learn-it",
                    color = Color(0xFF323499),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 48.dp) // Offset for back icon balance
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun AccuracyCard(correctAnswers: Int, totalCards: Int) {
    val accuracy = (correctAnswers * 100) / totalCards
    val sweepAngle = (accuracy.toFloat() / 100f) * 360f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Professional Circular Progress
                Canvas(modifier = Modifier.size(180.dp)) {
                    // Background Track
                    drawArc(
                        color = Color(0xFFF0F0F5),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Progress Arc with Gradient
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color(0xFF9D5CE6), Color(0xFF5E5CE6), Color(0xFF9D5CE6))
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner Shadow/Glow Effect for Text Area
                Surface(
                    modifier = Modifier.size(130.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$accuracy%",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF323499),
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "ACCURACY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Gray.copy(alpha = 0.6f),
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultStatsCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.LightGray.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF9D5CE6), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun StreakCard(streakDays: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.LightGray.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFF9D5CE6), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "New Streak", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
            Text(text = "$streakDays Days", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323499))
        }
    }
}

@Composable
fun ScoreUploadedCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.2f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5FF))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFF5E5CE6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Score Uploaded", fontWeight = FontWeight.Bold, color = Color(0xFF323499), fontSize = 15.sp)
                Text(text = "You are currently ranked #5 globally.", color = Color.Gray, fontSize = 13.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun XpProgressSection(level: Int, currentXp: Int, maxXp: Int, gainedXp: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = "Level $level", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323499))
            Text(text = "+$gainedXp XP Gained", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LinearProgressIndicator(
            progress = { currentXp.toFloat() / maxXp.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = Color(0xFF5E5CE6),
            trackColor = Color(0xFFF0F0F5),
            strokeCap = StrokeCap.Round
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "%,d XP".format(currentXp), fontSize = 13.sp, color = Color.Gray)
            Text(text = "%,d XP".format(maxXp), fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ResultBottomNavigationBar(
    onHomeClick: () -> Unit,
    onFlashcardsClick: () -> Unit,
    onStudyClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.Gray) },
            label = { Text("Home", color = Color.Gray) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = onFlashcardsClick,
            icon = { Icon(Icons.Default.Style, contentDescription = null, tint = Color.Gray) },
            label = { Text("Flashcards", color = Color.Gray) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = true,
            onClick = onStudyClick,
            icon = { 
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook, 
                    contentDescription = null, 
                    tint = Color(0xFF5E5CE6),
                    modifier = Modifier.size(26.dp).offset(y = (-4).dp)
                )
            },
            label = { 
                Text(
                    text = "Study", 
                    fontWeight = FontWeight.Bold, 
                    color = Color(0xFF5E5CE6),
                    modifier = Modifier.offset(y = (-2).dp)
                ) 
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = onLeaderboardClick,
            icon = { Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.Gray) },
            label = { Text("Leaderboard", color = Color.Gray) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SessionResultPreview() {
    SessionResultScreen()
}
