package com.learnit.app.presentation.screen

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
    email: String? = null
) {
    // ponytail: level/xp/achievements/stats are static placeholders — no backend tracks
    // XP, streaks, or achievements yet. `name` and `email` below are real (from the account).
    // Wire the rest to a real profile/stats source if that data starts being persisted.
    val user = remember(name) {
        mutableStateOf(
            UserProfile(
                name = name ?: "Learner",
                level = 12,
                xp = 1250
            )
        )
    }

    val achievements = remember {
        mutableStateListOf(
            Achievement("Early Bird", Icons.Default.LightMode),
            Achievement("7-Day Streak", Icons.AutoMirrored.Filled.TrendingUp),
            Achievement("Master of Biology", Icons.Default.Biotech)
        )
    }

    Scaffold(
        containerColor = Color(0xFFFBFBFF),
        topBar = {
            Box {
                CommonTopAppBar(
                    title = "Profile",
                    actionIcon = Icons.Default.Settings,
                    showProfile = false,
                    showBack = true,
                    onBackClick = onBackClick,
                    onActionClick = onSettingsClick,
                    onProfileClick = { /* Already in Profile */ }
                )
            }
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
            // Profile Header
            item {
                ProfileHeader(user.value, email)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Statistics Grid
            item {
                StatisticsSection()
                Spacer(modifier = Modifier.height(40.dp))
            }

            // Achievements Section
            item {
                AchievementsSection(achievements)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Menu Section
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

        // Avatar with soft purple border
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(
                    width = 4.dp,
                    color = Color(0xFFE0E0F5),
                    shape = CircleShape
                )
                .padding(8.dp)
                .background(Color(0xFF323499), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = user.name,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        if (email != null) {
            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Color(0xFF5E5CE6).copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Level ${user.level}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = Color(0xFF5E5CE6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${user.xp} XP",
                color = Color.Gray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatisticsSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatisticCard(
                icon = Icons.Default.Whatshot,
                label = "Days Streak",
                value = "8",
                modifier = Modifier.weight(1f)
            )
            StatisticCard(
                icon = Icons.Default.School,
                label = "Cards Mastered",
                value = "450",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatisticCard(
                icon = Icons.Default.AccessTime,
                label = "Study Time",
                value = "12h 30m",
                modifier = Modifier.weight(1f)
            )
            StatisticCard(
                icon = Icons.Default.EmojiEvents,
                label = "Global Rank",
                value = "#5",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatisticCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.15f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF9D5CE6).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF9D5CE6),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun AchievementsSection(achievements: List<Achievement>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Achievements",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(achievements) { achievement ->
                AchievementCard(achievement)
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier
            .size(110.dp, 130.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.LightGray.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF5E5CE6).copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = achievement.icon,
                    contentDescription = null,
                    tint = Color(0xFF5E5CE6),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = achievement.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}


@Composable
fun ProfileMenuSection(
    onSettingsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ProfileMenuItem(
                icon = Icons.Default.Person,
                label = "Account Settings",
                onClick = onSettingsClick
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF5F5F9))
            ProfileMenuItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                onClick = onNotificationClick
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF5F5F9))
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Help,
                label = "Help & Support",
                onClick = onHelpSupportClick
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF5F5F9))
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Logout",
                color = Color.Red,
                onClick = onLogout
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    color: Color = Color.Gray,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(if (color == Color.Red) Color.Red.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (color == Color.Red) Color.Red else Color(0xFF2D2D2D)
        )
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    MaterialTheme {
        ProfileScreen()
    }
}
