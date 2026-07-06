package com.learnit.app.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class UserSettingsProfile(
    val fullName: String,
    val email: String,
    val membership: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onEditPhotoClick: () -> Unit = {},
    initialName: String? = null,
    initialEmail: String? = null
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf(initialName ?: "Learner") }
    var email by remember { mutableStateOf(initialEmail ?: "") }
    var dailyGoal by remember { mutableIntStateOf(45) }
    var studyReminderEnabled by remember { mutableStateOf(true) }
    var focusModeEnabled by remember { mutableStateOf(false) }
    
    var showGoalDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    if (showGoalDialog) {
        DailyGoalDialog(
            currentGoal = dailyGoal,
            onDismiss = { showGoalDialog = false },
            onGoalSelected = { 
                dailyGoal = it
                showGoalDialog = false
            }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change Password") },
            text = { Text("Change Password feature coming soon") },
            confirmButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SettingsTopBar(
                onBackClick = onBackClick,
                onSaveClick = {
                    Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
                    onBackClick()
                }
            )
        },
        bottomBar = {
            SettingsBottomNavigationBar(
                onHomeClick = onHomeClick,
                onFlashcardsClick = onFlashcardsClick,
                onStudyClick = onStudyClick,
                onLeaderboardClick = onLeaderboardClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBFBFF)),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                ProfileHeader(
                    user = UserSettingsProfile(
                        fullName = fullName,
                        email = email,
                        membership = "Premium Learner"
                    ),
                    onEditPhotoClick = {
                        onEditPhotoClick()
                        Toast.makeText(context, "Edit photo clicked", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                PersonalInformationSection(
                    name = fullName,
                    onNameChange = { fullName = it },
                    email = email,
                    onEmailChange = { email = it },
                    onChangePasswordClick = { showPasswordDialog = true }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                StudyPreferencesSection(
                    dailyGoal = dailyGoal,
                    onAdjustClick = { showGoalDialog = true },
                    studyReminderEnabled = studyReminderEnabled,
                    onReminderToggle = { studyReminderEnabled = it },
                    focusModeEnabled = focusModeEnabled,
                    onFocusToggle = { focusModeEnabled = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(onBackClick: () -> Unit, onSaveClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Settings",
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
            TextButton(onClick = onSaveClick) {
                Text(
                    text = "Save",
                    color = Color(0xFF5E5CE6),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun ProfileHeader(
    user: UserSettingsProfile,
    onEditPhotoClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val initials = user.fullName.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()

        Box(
            modifier = Modifier.size(100.dp)
        ) {
            // Main Profile Circle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF323499), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Edit Photo Button (Bottom Right Overlay)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .background(Color(0xFF5E5CE6), CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onEditPhotoClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Edit Profile Photo",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.fullName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = user.membership,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun PersonalInformationSection(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    onChangePasswordClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF5E5CE6),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PERSONAL INFORMATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Full Name", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE8E8F5),
                        focusedBorderColor = Color(0xFF5E5CE6)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("Email Address", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE8E8F5),
                        focusedBorderColor = Color(0xFF5E5CE6)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onChangePasswordClick),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Change Password", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Last changed 3 months ago", fontSize = 13.sp, color = Color.Gray)
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun StudyPreferencesSection(
    dailyGoal: Int,
    onAdjustClick: () -> Unit,
    studyReminderEnabled: Boolean,
    onReminderToggle: (Boolean) -> Unit,
    focusModeEnabled: Boolean,
    onFocusToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = Color(0xFF5E5CE6),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "STUDY PREFERENCES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Daily Learning Goal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Daily Learning Goal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("$dailyGoal minutes per day", fontSize = 13.sp, color = Color.Gray)
                    }
                    Surface(
                        onClick = onAdjustClick,
                        color = Color(0xFFF0F0FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Adjust",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color(0xFF5E5CE6),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF5F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                // Study Reminders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Study Reminders", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Notifications for daily goals", fontSize = 13.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = studyReminderEnabled,
                        onCheckedChange = onReminderToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF5E5CE6)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF5F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                // Focus Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Focus Mode", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Disable other apps while learning", fontSize = 13.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = focusModeEnabled,
                        onCheckedChange = onFocusToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF5E5CE6)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DailyGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onGoalSelected: (Int) -> Unit
) {
    val goals = listOf(15, 30, 45, 60, 90)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Learning Goal", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                goals.forEach { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGoalSelected(goal) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = goal == currentGoal,
                            onClick = { onGoalSelected(goal) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF5E5CE6))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$goal minutes", fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun SettingsBottomNavigationBar(
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
            selected = false,
            onClick = onStudyClick,
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color.Gray) },
            label = { Text("Study", color = Color.Gray) },
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
fun SettingsPreview() {
    SettingsScreen()
}
