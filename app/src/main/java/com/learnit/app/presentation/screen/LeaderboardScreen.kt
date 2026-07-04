package com.learnit.app.presentation.screen

import com.learnit.app.presentation.component.CommonTopAppBar
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val subtitle: String,
    val score: String,
    val isMe: Boolean = false,
    val trend: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val topUsers = listOf(
        LeaderboardUser(2, "Alex Rivera", "", "14,200 pts"),
        LeaderboardUser(1, "Sarah Chen", "", "16,850 pts"),
        LeaderboardUser(3, "Marcus Ng", "", "12,940 pts")
    )

    val otherUsers = listOf(
        LeaderboardUser(4, "Jessica Wu", "Study Streak: 15 Days", "11,200"),
        LeaderboardUser(5, "Kevin Wijaya (You)", "Top 5% this week", "10,850", isMe = true, trend = "+450"),
        LeaderboardUser(6, "David Smith", "Level 24 Scholar", "9,400"),
        LeaderboardUser(7, "Elena Petrova", "Mathematics Expert", "8,920"),
        LeaderboardUser(8, "Kenji Tanaka", "Polyglot Rank", "8,150"),
        LeaderboardUser(9, "Maria Garcia", "Biology Enthusiast", "7,800"),
        LeaderboardUser(10, "Ahmed Hassan", "Flashcard Master", "7,450"),
        LeaderboardUser(11, "Liam O'Connor", "Streak King: 20 Days", "7,100"),
        LeaderboardUser(12, "Yuki Sato", "Chemistry Pro", "6,800"),
        LeaderboardUser(13, "Emma Wilson", "Daily Learner", "6,500"),
        LeaderboardUser(14, "Luca Bianchi", "Consistent Study", "6,200"),
        LeaderboardUser(15, "Chloe Dubois", "Top 10% Overall", "5,900")
    )

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
            LeaderboardBottomNavigationBar(
                onHomeClick = onHomeClick,
                onFlashcardsClick = onFlashcardsClick,
                onStudyClick = onStudyClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBFBFF)),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Global Rankings",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Rise to the top of the Learn-it community",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            item {
                TopThreePodium(topUsers)
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("RANK LEARNER", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("SCORE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            }

            itemsIndexed(otherUsers) { _, user ->
                LeaderboardItem(user, onClick = { })
            }
        }
    }
}

@Composable
fun TopThreePodium(users: List<LeaderboardUser>) {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Alex Rivera (Rank 2)
            users.find { it.rank == 2 }?.let {
                PodiumUser(it, delay = 0)
            }
            // Sarah Chen (Rank 1)
            users.find { it.rank == 1 }?.let {
                PodiumUser(it, delay = 500, isFirst = true)
            }
            // Marcus Ng (Rank 3)
            users.find { it.rank == 3 }?.let {
                PodiumUser(it, delay = 1000)
            }
        }
    }
}

@Composable
fun PodiumUser(user: LeaderboardUser, delay: Int, isFirst: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = { })
    ) {
        FloatingAvatar(userName = user.name, delay = delay, isFirst = isFirst)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = user.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = user.score, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF5E5CE6))
    }
}

@Composable
fun FloatingAvatar(userName: String, delay: Int, isFirst: Boolean) {
    val initials = userName.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    val infiniteTransition = rememberInfiniteTransition(label = "Floating")
    
    val translationY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Y"
    )

    val rotationZ by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, delayMillis = delay, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Z"
    )

    Box(contentAlignment = Alignment.TopEnd) {
        Box(
            modifier = Modifier
                .size(if (isFirst) 85.dp else 70.dp)
                .graphicsLayer {
                    this.translationY = translationY
                    this.rotationZ = rotationZ
                }
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = if (isFirst) listOf(Color(0xFFFFD700), Color(0xFFFFA500)) else listOf(Color.LightGray, Color.Gray)
                    ),
                    shape = CircleShape
                )
                .padding(4.dp)
                .background(if (isFirst) Color(0xFF323499) else Color(0xFFE8E8F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = if (isFirst) Color.White else Color.Gray,
                fontSize = if (isFirst) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Rank Badge
        Box(
            modifier = Modifier
                .offset(x = 4.dp, y = 4.dp)
                .size(28.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isFirst) listOf(Color(0xFFFFD700), Color(0xFFFFA500)) 
                                else if (delay == 0) listOf(Color(0xFFC0C0C0), Color(0xFF808080))
                                else listOf(Color(0xFFCD7F32), Color(0xFF8B4513))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFirst) Icons.Default.EmojiEvents else Icons.Default.Stars,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun LeaderboardItem(user: LeaderboardUser, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color.LightGray.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isMe) Color(0xFFF5F5FF) else Color.White
        ),
        border = if (user.isMe) BorderStroke(1.5.dp, Color(0xFF5E5CE6).copy(alpha = 0.6f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.rank.toString(),
                modifier = Modifier.width(32.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (user.isMe) Color(0xFF5E5CE6) else Color.Black
            )
            
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(if (user.isMe) Color(0xFF323499) else Color(0xFFE8E8F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val initials = user.name.replace("(You)", "").trim().split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString("")
                    .uppercase()

                Text(
                    text = initials,
                    color = if (user.isMe) Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (user.isMe) Color.Black else Color.Black
                )
                Text(text = user.subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = user.score,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF5E5CE6)
                )
                user.trend?.let {
                    Text(
                        text = "⤴ $it",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34A853)
                    )
                }
            }
        }
    }
}


@Composable
fun LeaderboardBottomNavigationBar(
    onHomeClick: () -> Unit,
    onFlashcardsClick: () -> Unit,
    onStudyClick: () -> Unit
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
            selected = false,
            onClick = onStudyClick,
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color.Gray) },
            label = { Text("Study", color = Color.Gray) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { 
                Icon(
                    imageVector = Icons.Default.BarChart, 
                    contentDescription = null, 
                    tint = Color(0xFF5E5CE6),
                    modifier = Modifier.size(26.dp).offset(y = (-4).dp)
                ) 
            },
            label = { 
                Text(
                    text = "Leaderboard", 
                    fontWeight = FontWeight.Bold, 
                    color = Color(0xFF5E5CE6),
                    modifier = Modifier.offset(y = (-2).dp)
                ) 
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardPreview() {
    LeaderboardScreen()
}
