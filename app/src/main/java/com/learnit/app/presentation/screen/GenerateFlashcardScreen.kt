package com.learnit.app.presentation.screen

import com.learnit.app.presentation.component.CommonTopAppBar
import com.learnit.app.presentation.component.AppBottomNavBar
import com.learnit.app.presentation.component.NavTab
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateFlashcardScreen(
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    isGenerating: Boolean = false,
    errorMessage: String? = null,
    onGenerate: (topic: String, category: String, count: Int) -> Unit = { _, _, _ -> }
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
            AppBottomNavBar(
                current = NavTab.FLASHCARDS,
                onHome = onHomeClick,
                onFlashcards = onStudyClick,
                onCreate = {},
                onLeaderboard = onLeaderboardClick,
                onProfile = onProfileClick,
                showCreate = false
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFBFBFF))
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Create Magic Cards",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Transform your lecture notes or any topic into high-performance flashcards instantly.",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Form Card
                GenerateFormCard(onGenerateClick = onGenerate, isGenerating = isGenerating)

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorMessage, color = Color(0xFFD32F2F), fontSize = 14.sp, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Loading Overlay
            AnimatedVisibility(
                visible = isGenerating,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color(0xFF5E5CE6),
                strokeWidth = 4.dp,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "AI is working its magic...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF323499)
            )
            Text(
                text = "Crafting high-quality flashcards for you",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun GenerateFormCard(onGenerateClick: (topic: String, category: String, count: Int) -> Unit, isGenerating: Boolean = false) {
    var category by remember { mutableStateOf("") }
    var numberOfCardsStr by remember { mutableStateOf("5 Cards") }
    var topicText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            CategoryDropdown(selected = category, onSelected = { category = it })
            
            Spacer(modifier = Modifier.height(20.dp))
            
            NumberOfCardsDropdown(selected = numberOfCardsStr, onSelected = { numberOfCardsStr = it })
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "STUDY TOPIC OR TEXT CONTENT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF5F5F9), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = topicText,
                    onValueChange = { topicText = it },
                    placeholder = { 
                        Text(
                            "Paste your notes here or type a topic like 'Photosynthesis in C4 plants'...",
                            fontSize = 14.sp,
                            color = Color.LightGray
                        ) 
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color(0xFF5E5CE6)
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    val count = numberOfCardsStr.split(" ")[0].toIntOrNull() ?: 5
                    onGenerateClick(topicText, category, count)
                },
                enabled = topicText.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF5E5CE6).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E5CE6))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Generate Flashcards", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(selected: String, onSelected: (String) -> Unit) {
    val categories = listOf(
        "Computer Science",
        "Biology",
        "Chemistry",
        "Physics",
        "Mathematics",
        "History",
        "Engineering",
        "Business",
        "Economics",
        "Law",
        "Medicine",
        "Psychology",
        "Literature",
        "Art & Design",
        "Philosophy",
        "Geography",
        "Languages",
        "Sociology",
        "Politics",
        "Accounting"
    )
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Category", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE8E8F5),
                    focusedBorderColor = Color(0xFF5E5CE6)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberOfCardsDropdown(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("5 Cards", "10 Cards", "15 Cards", "20 Cards")
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Number of Cards", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select count") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE8E8F5),
                    focusedBorderColor = Color(0xFF5E5CE6)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GenerateFlashcardPreview() {
    GenerateFlashcardScreen()
}
