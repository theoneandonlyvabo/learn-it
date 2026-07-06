package com.learnit.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnit.app.presentation.component.AppBottomNavBar
import com.learnit.app.presentation.component.CommonTopAppBar
import com.learnit.app.presentation.component.NavTab

data class UserProfile(
    val name: String,
    val level: Int,
    val xp: Int,
    val profileImage: String? = null
)

data class Achievement(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    name: String? = null,
    email: String? = null,
    totalStudyTimeSeconds: Long = 0,
    learningScore: Int = 0,
    flashcardCount: Int = 0,
    daysStreak: Int = 0,
    cardsMastered: Int = 0,
    globalRank: String = "-",
    achievements: List<Achievement> = emptyList()
) {
    val user = remember(name) {
        UserProfile(
            name = name ?: "Learner",
            level = 12,
            xp = 1250
        )
    }

    val finalAchievements = if (achievements.isEmpty()) {
        listOf(
            Achievement("Early Bird", Icons.Default.LightMode),
            Achievement("7-Day Streak", Icons.AutoMirrored.Filled.TrendingUp),
            Achievement("Master of Biology", Icons.Default.Biotech)
        )
    } else {
        achievements
    }

    Scaffold(
        containerColor = Color(0xFFFBFBFF),
        topBar = {
            CommonTopAppBar(
                title = "Profile",
                actionIcon = Icons.Default.Settings,
                showProfile = false,
                showBack = showBack,
                onBackClick = onBackClick,
                onActionClick = onSettingsClick,
                onProfileClick = { }
            )
        },
        bottomBar = {
            AppBottomNavBar(
                current = NavTab.PROFILE,
                onHome = onHomeClick,
                onFlashcards = onFlashcardsClick,
                onCreate = onCreateClick,
                onLeaderboard = onLeaderboardClick,
                onProfile = { }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                ProfileHeader(user, email)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                StatisticsSection(
                    totalStudyTimeSeconds = totalStudyTimeSeconds,
                    daysStreak = daysStreak,
                    cardsMastered = cardsMastered,
                    globalRank = globalRank
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                AchievementsSection(finalAchievements)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                ProfileMenuSection(
                    onSettingsClick = onSettingsClick,
                    onNotificationClick = onNotificationClick,
                    onHelpSupportClick = onHelpSupportClick,
                    onLogout = onLogout
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(user: UserProfile, email: String? = null) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val initials = user.name.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()

        Box(
            modifier = Modifier
                .size(120.dp)
                .border(4.dp, Color(0xFFE0E0F5), CircleShape)
                .padding(8.dp)
                .background(Color(0xFF323499), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(user.name, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        if (email != null) {
            Text(email, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFF5E5CE6).copy(alpha = 0.1f), shape = RoundedCornerShape(24.dp)) {
                Text("Level ${user.level}", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = Color(0xFF5E5CE6), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("${user.xp} XP", color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatisticsSection(totalStudyTimeSeconds: Long, daysStreak: Int, cardsMastered: Int, globalRank: String) {
    val hours = totalStudyTimeSeconds / 3600
    val minutes = (totalStudyTimeSeconds % 3600) / 60
    val timeStr = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatisticCard(Icons.Default.Whatshot, "Days Streak", daysStreak.toString(), Modifier.weight(1f))
            StatisticCard(Icons.Default.School, "Cards Mastered", cardsMastered.toString(), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatisticCard(Icons.Default.AccessTime, "Study Time", timeStr, Modifier.weight(1f))
            StatisticCard(Icons.Default.EmojiEvents, "Global Rank", if (globalRank.startsWith("#")) globalRank else "#$globalRank", Modifier.weight(1f))
        }
    }
}

@Composable
fun StatisticCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.15f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFF9D5CE6).copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color(0xFF9D5CE6), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun AchievementsSection(achievements: List<Achievement>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Achievements", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(16.dp))
        
        // Using Column + Rows to create a simple Grid (3 columns) since horizontal scroll is an issue
        val chunks = achievements.chunked(3)
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            chunks.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { achievement ->
                        AchievementCard(achievement, modifier = Modifier.weight(1f))
                    }
                    // Fill empty slots in the last row if needed
                    if (rowItems.size < 3) {
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(130.dp).shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.LightGray.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFF5E5CE6).copy(alpha = 0.08f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(achievement.icon, null, tint = Color(0xFF5E5CE6), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(achievement.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 16.sp)
        }
    }
}

@Composable
fun ProfileMenuSection(onSettingsClick: () -> Unit, onNotificationClick: () -> Unit, onHelpSupportClick: () -> Unit, onLogout: () -> Unit) {
    Card(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.15f)), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ProfileMenuItem(Icons.Default.Person, "Account Settings", onClick = onSettingsClick)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF5F5F9))
            ProfileMenuItem(Icons.Default.Notifications, "Notifications", onClick = onNotificationClick)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF5F5F9))
            ProfileMenuItem(Icons.AutoMirrored.Filled.Help, "Help & Support", onClick = onHelpSupportClick)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF5F5F9))
            ProfileMenuItem(Icons.AutoMirrored.Filled.Logout, "Logout", Color.Red, onClick = onLogout)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, color: Color = Color.Gray, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 24.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).background(if (color == Color.Red) Color.Red.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = if (color == Color.Red) Color.Red else Color(0xFF2D2D2D))
        Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    ProfileScreen()
}
