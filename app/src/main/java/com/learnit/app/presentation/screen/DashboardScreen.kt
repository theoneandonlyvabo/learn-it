package com.learnit.app.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.runtime.produceState
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage
import com.learnit.app.domain.model.DeckSummary
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnit.app.R
import com.learnit.app.presentation.component.AppBottomNavBar
import com.learnit.app.presentation.component.CommonTopAppBar
import com.learnit.app.presentation.component.NavTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onStudyClick: () -> Unit = {},
    onStartStudyClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onCreateFlashcardClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onLeaderboardActionClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onViewAllDecksClick: () -> Unit = {},
    onFlashcardsSummaryClick: () -> Unit = {},
    onLearningScoreSummaryClick: () -> Unit = {},
    userName: String? = null,
    flashcardCount: Int = 0,
    learningScore: Int = 0,
    decks: List<DeckSummary> = emptyList(),
    onDeckClick: (DeckSummary) -> Unit = {},
    resolveDeckImage: suspend (topic: String) -> String? = { null }
) {
    Scaffold(
        topBar = { 
            CommonTopAppBar(
                onActionClick = onNotificationClick,
                onProfileClick = onProfileClick
            ) 
        },
        bottomBar = {
            AppBottomNavBar(
                current = NavTab.HOME,
                onHome = { },
                onFlashcards = onFlashcardsClick,
                onCreate = onCreateFlashcardClick,
                onLeaderboard = onLeaderboardClick,
                onProfile = onProfileClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBFBFF))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Hello, ${userName ?: "there"}",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Ready to level up your knowledge today?",
                fontSize = 16.sp,
                color = Color.Gray.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    label = "FLASHCARDS",
                    value = flashcardCount.toString(),
                    icon = Icons.Default.Collections,
                    modifier = Modifier.weight(1f),
                    onClick = onFlashcardsSummaryClick
                )
                SummaryCard(
                    label = "LEARNING SCORE",
                    value = "%,d pts".format(learningScore),
                    icon = Icons.Default.Timeline,
                    modifier = Modifier.weight(1f),
                    onClick = onLearningScoreSummaryClick
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Quick Actions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuickActionCard(
                label = "Create Flashcard",
                icon = Icons.Default.Add,
                iconContainerColor = Color.White.copy(alpha = 0.22f),
                iconColor = Color.White,
                containerColor = Color(0xFF5E5CE6),
                labelColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreateFlashcardClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard(
                    label = "Start Study",
                    icon = Icons.Default.PlayArrow,
                    iconContainerColor = Color(0xFFF3E8FF),
                    iconColor = Color(0xFF9D5CE6),
                    modifier = Modifier.weight(1f),
                    isCompact = true,
                    onClick = onStartStudyClick
                )
                QuickActionCard(
                    label = "Leaderboard",
                    icon = Icons.Default.BarChart,
                    iconContainerColor = Color(0xFFE8F6FF),
                    iconColor = Color(0xFF5CA3E6),
                    modifier = Modifier.weight(1f),
                    isCompact = true,
                    onClick = onLeaderboardActionClick
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Decks",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                TextButton(onClick = onViewAllDecksClick) {
                    Text("View All", color = Color(0xFF5E5CE6), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (decks.isEmpty()) {
                Text(
                    text = "No decks yet. Generate your first flashcards to get started.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                decks.forEach { deck ->
                    RecentDeckItem(
                        title = deck.title,
                        stats = "${deck.cardCount} Cards • ${deck.lastStudied}",
                        topic = deck.title,
                        resolveDeckImage = resolveDeckImage,
                        onClick = { onDeckClick(deck) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = modifier.shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(32.dp).background(Color(0xFFF0F0FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF5E5CE6), modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray.copy(alpha = 0.6f))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323499))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionCard(
    label: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    containerColor: Color = Color.White,
    labelColor: Color = Color(0xFF323499),
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        if (!isCompact) {
            Row(
                modifier = Modifier.padding(18.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(iconContainerColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = labelColor)
            }
        } else {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(iconContainerColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentDeckItem(
    title: String,
    stats: String,
    imageRes: Int? = null,
    topic: String? = null,
    resolveDeckImage: suspend (topic: String) -> String? = { null },
    onClick: () -> Unit = {}
) {
    val imageUrl by produceState<String?>(initialValue = null, topic) {
        value = topic?.let { resolveDeckImage(it) }
    }
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.LightGray.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF5F5F9)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageRes != null -> Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    imageUrl != null -> AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    else -> Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFFC0C0D0)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(18.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stats, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DashboardScreen()
}
