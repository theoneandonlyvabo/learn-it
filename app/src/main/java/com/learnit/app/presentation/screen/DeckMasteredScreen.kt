package com.learnit.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==================================================
// DATA CLASSES
// ==================================================

data class DeckMasteredResult(
    val totalCards: Int,
    val completionXp: Int,
    val timeBonusXp: Int,
    val streakXp: Int,
    val gainedXp: Int,
    val currentLevel: Int,
    val nextLevel: Int,
    val currentXp: Int,
    val maxXp: Int
)

// ==================================================
// MAIN SCREEN
// ==================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckMasteredScreen(
    result: DeckMasteredResult = DeckMasteredResult(
        totalCards = 10,
        completionXp = 250,
        timeBonusXp = 100,
        streakXp = 100,
        gainedXp = 450,
        currentLevel = 12,
        nextLevel = 13,
        currentXp = 1250,
        maxXp = 1800
    ),
    onBackClick: () -> Unit = {},
    onBackToDecks: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {}
) {
    Scaffold(
        topBar = { DeckMasteredTopBar(onBackClick) },
        bottomBar = {
            DeckBottomNavigationBar(
                onHomeClick = onHomeClick,
                onFlashcardsClick = onFlashcardsClick,
                onLeaderboardClick = onLeaderboardClick
            )
        },
        containerColor = Color(0xFFFBFBFF)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HeaderSection()
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                TotalXpCard(gainedXp = result.gainedXp)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                DeckRecapCard(totalCards = result.totalCards)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                LevelProgressCard(
                    currentLevel = result.currentLevel,
                    nextLevel = result.nextLevel,
                    currentXp = result.currentXp,
                    maxXp = result.maxXp,
                    gainedXp = result.gainedXp
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                ActionButtons(
                    onBackToDecks = onBackToDecks,
                    onLeaderboardClick = onLeaderboardClick
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==================================================
// COMPONENTS
// ==================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckMasteredTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Learn-it",
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun HeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.2f))
                .background(Color.White, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF5E5CE6)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Deck Mastered!",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Text(
            text = "Your knowledge graph just leveled up.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TotalXpCard(gainedXp: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(20.dp, RoundedCornerShape(32.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF7B78FF), Color(0xFF5E5CE6))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TOTAL GAINED",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "+$gainedXp XP",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Decorative Line
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                )
            }
        }
    }
}

@Composable
fun DeckRecapCard(totalCards: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "DECK RECAP",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$totalCards Cards",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Processed by AI",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "100%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5E5CE6)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // ponytail: these per-skill bars are visual placeholders — ScoreCalculator only
                // produces one aggregate score, not per-dimension recall/retention/speed/accuracy
                // breakdowns. Wire real values if the backend starts tracking them.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecapProgress(label = "RECALL", modifier = Modifier.weight(1f))
                    RecapProgress(label = "RETENTION", modifier = Modifier.weight(1f))
                    RecapProgress(label = "SPEED", modifier = Modifier.weight(1f))
                    RecapProgress(label = "ACCURACY", modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun RecapProgress(label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = Color(0xFF5E5CE6),
            trackColor = Color(0xFFF0F0F5)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}

@Composable
fun LevelProgressCard(
    currentLevel: Int,
    nextLevel: Int,
    currentXp: Int,
    maxXp: Int,
    gainedXp: Int
) {
    val progress = currentXp.toFloat() / maxXp.toFloat()
    val remainingXp = maxXp - currentXp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5FF))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF323499),
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "$currentLevel", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Next: Level $nextLevel",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF323499),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$remainingXp XP to go",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = Color(0xFF5E5CE6),
                trackColor = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "%,d XP".format(currentXp),
                    fontSize = 13.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "%,d XP".format(maxXp),
                    fontSize = 13.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    onBackToDecks: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = onBackToDecks,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E5CE6))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Back to Decks", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
        
        OutlinedButton(
            onClick = onLeaderboardClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            border = null,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
            Text(text = "View Leaderboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@Composable
fun DeckBottomNavigationBar(
    onHomeClick: () -> Unit,
    onFlashcardsClick: () -> Unit,
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
            onClick = { },
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
fun DeckMasteredPreview() {
    DeckMasteredScreen()
}
