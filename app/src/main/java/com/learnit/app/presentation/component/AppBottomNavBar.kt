package com.learnit.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NavTab { HOME, FLASHCARDS, LEADERBOARD, PROFILE }

private val Accent = Color(0xFF5E5CE6)
private val Inactive = Color(0xFF9AA0B4)

/**
 * Shared bottom navigation with a center-docked "Create" FAB (Material 3 pattern).
 * Home · Flashcards · [Create] · Leaderboard · Profile.
 */
@Composable
fun AppBottomNavBar(
    current: NavTab,
    onHome: () -> Unit,
    onFlashcards: () -> Unit,
    onCreate: () -> Unit,
    onLeaderboard: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFBFBFF)) // fills the FAB-overhang strip so no black shows behind
            .navigationBarsPadding()        // lift above the system gesture/home bar
            .height(90.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(Icons.Filled.Home, "Home", current == NavTab.HOME, onHome, Modifier.weight(1f))
                NavItem(Icons.Filled.Style, "Flashcards", current == NavTab.FLASHCARDS, onFlashcards, Modifier.weight(1f))
                Spacer(Modifier.weight(1f)) // reserved slot for the docked FAB
                NavItem(Icons.Filled.BarChart, "Leaderboard", current == NavTab.LEADERBOARD, onLeaderboard, Modifier.weight(1f))
                NavItem(Icons.Filled.Person, "Profile", current == NavTab.PROFILE, onProfile, Modifier.weight(1f))
            }
        }

        // Raised center Create button, straddling the top edge of the bar.
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .shadow(14.dp, CircleShape, spotColor = Accent, ambientColor = Accent)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF6F6DF2), Color(0xFF5E5CE6))))
                    .clickable(onClick = onCreate),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create flashcards",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.height(3.dp))
            Text("New Card", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) Accent else Inactive
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(23.dp))
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(if (selected) Accent else Color.Transparent)
        )
    }
}
