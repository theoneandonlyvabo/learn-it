package com.learnit.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.learnit.app.domain.model.DeckSummary
import com.learnit.app.presentation.component.AppBottomNavBar
import com.learnit.app.presentation.component.CommonTopAppBar
import com.learnit.app.presentation.component.NavTab

// Shared palette so text/accents stay consistent across the screen.
private val Accent = Color(0xFF5E5CE6)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF8A8A9A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    showBack: Boolean = false,
    userName: String? = null,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCreateDeckClick: () -> Unit = {},
    onStudyNowClick: (String) -> Unit = {},
    decks: List<DeckSummary> = emptyList(),
    resolveDeckImage: suspend (topic: String) -> String? = { null }
) {
    Scaffold(
        topBar = {
            CommonTopAppBar(
                userName = userName ?: "Learner",
                showBack = showBack,
                onBackClick = onBackClick,
                onActionClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        },
        bottomBar = {
            AppBottomNavBar(
                current = NavTab.FLASHCARDS,
                onHome = onHomeClick,
                onFlashcards = onFlashcardsClick,
                onCreate = onCreateDeckClick,
                onLeaderboard = onLeaderboardClick,
                onProfile = onProfileClick
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

            if (decks.isEmpty()) {
                item { EmptyDecks() }
            } else {
                items(decks, key = { it.deckId }) { deck ->
                    DeckCard(
                        title = deck.title,
                        cardCount = deck.cardCount,
                        lastStudied = deck.lastStudied,
                        resolveDeckImage = resolveDeckImage,
                        onStudyClick = { onStudyNowClick(deck.deckId) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(onCreateDeckClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "My Decks", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Manage and study your flashcard collections.",
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onCreateDeckClick,
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Create Deck", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DeckCard(
    title: String,
    cardCount: Int,
    lastStudied: String,
    resolveDeckImage: suspend (topic: String) -> String?,
    onStudyClick: () -> Unit
) {
    val imageUrl by produceState<String?>(initialValue = null, title) {
        value = resolveDeckImage(title)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFEDECFF), Color(0xFFDCDBFF)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = Accent.copy(alpha = 0.5f),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$cardCount Cards • $lastStudied",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStudyClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Study Now", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmptyDecks() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDECFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("No decks yet", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Tap Create Deck to generate your first set of flashcards.",
            fontSize = 13.sp,
            color = TextSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StudyPreview() {
    StudyScreen(
        decks = listOf(
            DeckSummary("quantum_1", "Quantum Computing", 9, "Not studied yet"),
            DeckSummary("history_1", "World History", 24, "Last studied 2h ago"),
        )
    )
}
