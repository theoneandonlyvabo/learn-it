package com.learnit.app.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnit.app.presentation.component.AppLogo

data class NotificationItem(
    val id: Int,
    val category: String,
    val message: String,
    val time: String,
    val isRead: Boolean,
    val type: String
)

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {}
) {
    val initialNotifications = remember {
        mutableStateListOf(
            NotificationItem(1, "STREAK UPDATE", "7 Day Streak! You're on fire! Keep it up to earn a bonus badge.", "2m ago", false, "Achievements"),
            NotificationItem(2, "SOCIAL", "Sarah Chen just overtook you on the Leaderboard. Study now to reclaim your spot!", "1h ago", false, "Achievements"),
            NotificationItem(3, "NEW CONTENT", "New deck 'Quantum Physics' is ready for you to explore.", "5h ago", true, "Study Reminders"),
            NotificationItem(4, "ROUTINE", "Time for your daily 10-minute study session. Your brain will thank you!", "Yesterday", true, "Study Reminders")
        )
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Achievements", "Study Reminders")

    val filteredNotifications = when (selectedTabIndex) {
        1 -> initialNotifications.filter { it.type == "Achievements" }
        2 -> initialNotifications.filter { it.type == "Study Reminders" }
        else -> initialNotifications
    }

    Scaffold(
        topBar = { NotificationTopBar(onBackClick) },
        bottomBar = {
            NotificationBottomNavigationBar(
                onHomeClick = onHomeClick,
                onFlashcardsClick = onFlashcardsClick,
                onStudyClick = onStudyClick,
                onLeaderboardClick = onLeaderboardClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBFBFF))
        ) {
            NotificationTabs(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )

            if (filteredNotifications.isEmpty()) {
                EmptyNotificationView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { notification ->
                        NotificationCard(
                            notification = notification,
                            onViewRankingClick = onLeaderboardClick,
                            onClick = {
                                val index = initialNotifications.indexOfFirst { it.id == notification.id }
                                if (index != -1 && !initialNotifications[index].isRead) {
                                    initialNotifications[index] = initialNotifications[index].copy(isRead = true)
                                }
                            }
                        )
                    }
                    
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Menggunakan AppLogo versi abu-abu (grayscale)
                                AppLogo(logoSize = 36.dp, showShadow = false, grayscale = true)
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = "END OF ACTIVITY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray.copy(alpha = 0.8f),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Notifications",
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun NotificationTabs(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            Surface(
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .weight(if (index == 0) 0.7f else 1f),
                shape = CircleShape, // Gunakan parameter shape langsung agar lengkung sempurna
                color = if (isSelected) Color(0xFF5E5CE6) else Color(0xFFF0F0F5)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.dp), // Lebih tipis agar terkesan profesional
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onViewRankingClick: () -> Unit,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (notification.isRead) Color(0xFFF5F5F9) else Color.White,
        label = "BgColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (notification.isRead) 1.dp else 8.dp, // Shadow lebih kuat untuk yang belum dibaca
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFF5E5CE6).copy(alpha = 0.25f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (!notification.isRead) BorderStroke(1.5.dp, Color(0xFF5E5CE6).copy(alpha = 0.15f)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = when (notification.category) {
                            "STREAK UPDATE" -> Color(0xFFFFF0E6)
                            "SOCIAL" -> Color(0xFFF3E8FF)
                            else -> Color(0xFFF0F0F5)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.category) {
                        "STREAK UPDATE" -> Icons.Default.Whatshot
                        "SOCIAL" -> Icons.Default.BarChart
                        "NEW CONTENT" -> Icons.Default.MenuBook
                        else -> Icons.Default.Timer
                    },
                    contentDescription = null,
                    tint = when (notification.category) {
                        "STREAK UPDATE" -> Color(0xFFFD7E14)
                        "SOCIAL" -> Color(0xFF9D5CE6)
                        else -> Color(0xFF5E5CE6)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (notification.isRead) Color.Gray else Color(0xFF5E5CE6),
                        letterSpacing = 0.5.sp
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = notification.time,
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                        if (!notification.isRead) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF5E5CE6), CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (notification.isRead) Color.Gray else Color.Black,
                    lineHeight = 20.sp
                )
                
                if (notification.category == "SOCIAL") {
                    TextButton(
                        onClick = onViewRankingClick,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(32.dp).padding(top = 4.dp)
                    ) {
                        Text(
                            text = "View Ranking >",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5E5CE6)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No notifications yet",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun NotificationBottomNavigationBar(
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
            label = { Text("Home", color = Color.Gray) }
        )
        NavigationBarItem(
            selected = false,
            onClick = onFlashcardsClick,
            icon = { Icon(Icons.Default.Style, contentDescription = null, tint = Color.Gray) },
            label = { Text("Flashcards", color = Color.Gray) }
        )
        NavigationBarItem(
            selected = false,
            onClick = onStudyClick,
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color.Gray) },
            label = { Text("Study", color = Color.Gray) }
        )
        NavigationBarItem(
            selected = false,
            onClick = onLeaderboardClick,
            icon = { Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.Gray) },
            label = { Text("Leaderboard", color = Color.Gray) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPreview() {
    NotificationScreen()
}
