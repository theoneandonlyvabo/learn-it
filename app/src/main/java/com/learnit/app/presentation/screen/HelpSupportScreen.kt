package com.learnit.app.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnit.app.presentation.component.AppBottomNavBar
import com.learnit.app.presentation.component.NavTab

data class HelpTopic(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

data class FAQItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    showBack: Boolean = true,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onAppFeaturesClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }

    val faqItems = remember {
        mutableStateListOf(
            FAQItem("How do I reset my password?", "Go to Account Settings → Change Password and follow the instructions."),
            FAQItem("Can I learn offline?", "Some content can be downloaded and accessed offline."),
            FAQItem("Managing multiple devices", "You can log in on multiple devices using the same account.")
        )
    }

    Scaffold(
        topBar = { HelpSupportTopBar(showBack, onBackClick) },
        bottomBar = {
            AppBottomNavBar(
                current = NavTab.PROFILE, // Help is usually under profile
                onHome = onHomeClick,
                onFlashcards = onFlashcardsClick,
                onCreate = onCreateClick,
                onLeaderboard = onLeaderboardClick,
                onProfile = onProfileClick,
                showCreate = false
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBFBFF)),
            contentPadding = PaddingValues(24.dp)
        ) {
            // Header & Search
            item {
                SearchSection(
                    searchText = searchText,
                    onSearchChange = { searchText = it }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Browse Topics
            item {
                BrowseTopicsSection(
                    onAccountClick = onProfileClick,
                    onAppFeaturesClick = onAppFeaturesClick
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Popular Questions
            item {
                Text(
                    text = "Popular Questions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(faqItems) { index, item ->
                FaqCard(
                    item = item,
                    onToggle = {
                        faqItems[index] = faqItems[index].copy(isExpanded = !item.isExpanded)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportTopBar(showBack: Boolean, onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Help & Support",
                color = Color(0xFF323499),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF323499)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun SearchSection(searchText: String, onSearchChange: (String) -> Unit) {
    Column {
        Text(
            text = "How can we help?",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search FAQs, topics, and more...", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE8E8F5),
                focusedBorderColor = Color(0xFF5E5CE6)
            ),
            singleLine = true
        )
    }
}

@Composable
fun BrowseTopicsSection(
    onAccountClick: () -> Unit,
    onAppFeaturesClick: () -> Unit
) {
    val topics = listOf(
        HelpTopic("Account", "Profile & Security", Icons.Default.AccountCircle),
        HelpTopic("App Features", "Learning paths, AI Tutor...", Icons.Default.Star)
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Browse Topics", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            topics.forEach { topic ->
                TopicCard(
                    topic = topic,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (topic.title == "App Features") {
                            onAppFeaturesClick()
                        } else if (topic.title == "Account") {
                            onAccountClick()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TopicCard(
    topic: HelpTopic,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.4f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFF0F0FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = topic.icon, contentDescription = null, tint = Color(0xFF323499))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = topic.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text(text = topic.subtitle, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}

@Composable
fun FaqCard(item: FAQItem, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color.LightGray.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (item.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            AnimatedVisibility(visible = item.isExpanded) {
                Text(
                    text = item.answer,
                    modifier = Modifier.padding(top = 12.dp),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HelpSupportPreview() {
    HelpSupportScreen()
}
