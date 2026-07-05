package com.learnit.app.presentation.screen

import com.learnit.app.presentation.component.CommonTopAppBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnit.app.R

data class StudyDeck(
    val deckId: String,
    val title: String,
    val cardCount: Int,
    val lastStudied: String,
    val progress: Float,
    val imageRes: Int? = null,
    val isActive: Boolean = false,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCreateDeckClick: () -> Unit = {},
    onStudyNowClick: (String) -> Unit = {},
    onViewResultClick: (String) -> Unit = {},
    decks: List<StudyDeck> = emptyList()
) {
    Scaffold(
        topBar = { 
            CommonTopAppBar(
                showBack = showBack,
                onBackClick = onBackClick,
                onActionClick = onNotificationClick,
                onProfileClick = onProfileClick
            ) 
        },
        bottomBar = {
            StudyBottomNavigationBar(
                onHomeClick = onHomeClick,
                onFlashcardsClick = onFlashcardsClick,
                onLeaderboardClick = onLeaderboardClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBFBFF)),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                HeaderSection(onCreateDeckClick = onCreateDeckClick)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                AIRecommendationCard(onStudyClick = { onStudyNowClick("Biology: Cell Structures") })
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(decks, key = { it.deckId }) { deck ->
                DeckCard(
                    title = deck.title,
                    cardCount = deck.cardCount,
                    lastStudied = deck.lastStudied,
                    progress = deck.progress,
                    imageRes = deck.imageRes,
                    isActive = deck.isActive,
                    isCompleted = deck.isCompleted,
                    onStudyClick = { onStudyNowClick(deck.deckId) },
                    onViewResultClick = { onViewResultClick(deck.deckId) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(onCreateDeckClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "My Decks",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Manage and study your flashcard collections.",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Button(
            onClick = onCreateDeckClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E5CE6)),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Create Deck", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AIRecommendationCard(onStudyClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.2f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5FF))
    ) {
        Column {
            // Gambar Banner di atas (Full Width)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFFE8E8F5)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cell_structures),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Surface(
                    color = Color(0xFF5E5CE6),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "AI Recommendation",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Biology: Cell Structures", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323499))
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "You haven't reviewed this deck in 3 days.\nYour recall probability is dropping!",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onStudyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF323499)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Study Now", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "124 Cards", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun DeckCard(
    title: String,
    cardCount: Int,
    lastStudied: String,
    progress: Float,
    imageRes: Int? = null,
    isActive: Boolean,
    isCompleted: Boolean,
    onStudyClick: () -> Unit,
    onViewResultClick: () -> Unit = {}
) {
    val mainColor = if (isCompleted) Color(0xFF34A853) else Color(0xFF5E5CE6)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                }
                
                if (isActive || isCompleted) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        color = (if (isCompleted) Color(0xFF34A853) else Color.White).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = if (isCompleted) "Completed" else "Active Study",
                                color = if (isCompleted) Color.White else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "$cardCount Cards • Last studied $lastStudied", fontSize = 13.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = mainColor,
                    trackColor = Color(0xFFF0F0F5)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                if (isCompleted) {
                    // Jika selesai, tampilkan dua tombol: Review & View Score
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onViewResultClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF34A853))
                        ) {
                            Text("View Score", color = Color(0xFF34A853), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onStudyClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Review Again", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = onStudyClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E5CE6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Study Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StudyBottomNavigationBar(
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
fun StudyPreview() {
    StudyScreen(
        decks = listOf(
            StudyDeck("preview-1", "Quantum Physics", 86, "2h ago", 0.7f, imageRes = R.drawable.quantum_physics, isActive = true),
            StudyDeck("preview-2", "World History", 245, "1d ago", 0.4f, imageRes = R.drawable.world_history),
        )
    )
}
