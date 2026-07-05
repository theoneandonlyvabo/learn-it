package com.learnit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.learnit.app.presentation.component.AppLogo
import com.learnit.app.presentation.screen.*
import com.learnit.app.presentation.ui.theme.LearnItTheme
import dagger.hilt.android.AndroidEntryPoint

enum class Screen { 
    Splash, Login, Register, Dashboard, Study, StudySession, Result, 
    GenerateFlashcard, Leaderboard, Notification, Profile, HelpSupport, Settings, AppFeatures
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LearnItTheme {
                var currentScreen by remember { mutableStateOf(Screen.Splash) }
                var previousScreenForFlashcard by remember { mutableStateOf<Screen?>(null) }
                var previousScreenForLeaderboard by remember { mutableStateOf<Screen?>(null) }
                var previousScreenForStudy by remember { mutableStateOf<Screen?>(null) }
                var previousScreenForStudySession by remember { mutableStateOf<Screen?>(null) }
                var previousScreenForNotification by remember { mutableStateOf<Screen?>(null) }
                var previousScreenForProfile by remember { mutableStateOf<Screen?>(null) }
                var sessionResultData by remember { mutableStateOf<com.learnit.app.presentation.screen.SessionResult?>(null) }
                var selectedCardCount by remember { mutableIntStateOf(5) }
                
                var startSplashAnimation by remember { mutableStateOf(false) }
                val splashLogoAlpha by animateFloatAsState(
                    targetValue = if (startSplashAnimation) 1f else 0f,
                    animationSpec = tween(1200, delayMillis = 400),
                    label = "SplashLogoAlpha"
                )

                LaunchedEffect(Unit) {
                    startSplashAnimation = true
                }

                val isSplash = currentScreen == Screen.Splash

                val transitionProgress by animateFloatAsState(
                    targetValue = if (isSplash) 0f else 1f,
                    animationSpec = tween(1000, delayMillis = 1000, easing = EaseInOutCubic),
                    label = "MasterTransition"
                )

                val logoSize = lerpDp(80.dp, 40.dp, transitionProgress)
                val cardSize = lerpDp(80.dp, 80.dp, transitionProgress)
                val cardElevation = lerpDp(0.dp, 16.dp, transitionProgress)
                val verticalBias = lerpFloat(-0.18f, -0.82f, transitionProgress)
                val cardAlpha = transitionProgress

                Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFF))) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            EnterTransition.None togetherWith ExitTransition.None
                        },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            Screen.Splash -> SplashScreen(
                                onNavigate = { currentScreen = Screen.Login }
                            )
                            Screen.Login -> LoginScreen(
                                logoContent = { Box(Modifier.size(80.dp)) },
                                onRegisterClick = { currentScreen = Screen.Register },
                                onLoginSuccess = { currentScreen = Screen.Dashboard }
                            )
                            Screen.Register -> RegisterScreen(
                                onBackClick = { currentScreen = Screen.Login },
                                onSignInClick = { currentScreen = Screen.Login },
                                onSignUpSuccess = { currentScreen = Screen.Dashboard }
                            )
                            Screen.Dashboard -> DashboardScreen(
                                onStudyClick = { 
                                    previousScreenForStudy = null
                                    currentScreen = Screen.Study 
                                },
                                onStartStudyClick = {
                                    previousScreenForStudy = Screen.Dashboard
                                    currentScreen = Screen.Study
                                },
                                onFlashcardsClick = { 
                                    previousScreenForFlashcard = null
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onCreateFlashcardClick = {
                                    previousScreenForFlashcard = Screen.Dashboard
                                    currentScreen = Screen.GenerateFlashcard
                                },
                                onLeaderboardClick = { 
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.Leaderboard 
                                },
                                onLeaderboardActionClick = {
                                    previousScreenForLeaderboard = Screen.Dashboard
                                    currentScreen = Screen.Leaderboard
                                },
                                onNotificationClick = { 
                                    previousScreenForNotification = Screen.Dashboard
                                    currentScreen = Screen.Notification 
                                },
                                onProfileClick = { 
                                    previousScreenForProfile = Screen.Dashboard
                                    currentScreen = Screen.Profile 
                                },
                                onViewAllDecksClick = { 
                                    previousScreenForStudy = Screen.Dashboard
                                    currentScreen = Screen.Study 
                                },
                                onFlashcardsSummaryClick = { 
                                    previousScreenForStudy = Screen.Dashboard
                                    currentScreen = Screen.Study 
                                },
                                onLearningScoreSummaryClick = { 
                                    previousScreenForLeaderboard = Screen.Dashboard
                                    currentScreen = Screen.Leaderboard 
                                }
                            )
                            Screen.Study -> StudyScreen(
                                showBack = previousScreenForStudy != null,
                                onBackClick = {
                                    previousScreenForStudy?.let { currentScreen = it }
                                    previousScreenForStudy = null
                                },
                                onHomeClick = { 
                                    previousScreenForStudy = null
                                    currentScreen = Screen.Dashboard 
                                },
                                onFlashcardsClick = { 
                                    previousScreenForFlashcard = null
                                    previousScreenForStudy = null
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onLeaderboardClick = { 
                                    previousScreenForLeaderboard = null
                                    previousScreenForStudy = null
                                    currentScreen = Screen.Leaderboard 
                                },
                                onNotificationClick = { 
                                    previousScreenForNotification = Screen.Study
                                    currentScreen = Screen.Notification 
                                },
                                onProfileClick = { 
                                    previousScreenForProfile = Screen.Study
                                    currentScreen = Screen.Profile 
                                },
                                onCreateDeckClick = { 
                                    previousScreenForFlashcard = Screen.Study
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onStudyNowClick = { _ -> 
                                    previousScreenForStudySession = Screen.Study
                                    currentScreen = Screen.StudySession 
                                },
                                onViewResultClick = { _ -> 
                                    sessionResultData = com.learnit.app.presentation.screen.SessionResult(50, 50, "5m 20s", 12, 15, 1500, 2000, 500)
                                    currentScreen = Screen.Result
                                }
                            )
                            Screen.StudySession -> StudySessionScreen(
                                totalCards = selectedCardCount,
                                onBackClick = { 
                                    previousScreenForStudySession?.let { currentScreen = it }
                                    // Jika tidak ada history, default ke Study
                                    if (previousScreenForStudySession == null) currentScreen = Screen.Study
                                },
                                onFinishSession = { correct, total ->
                                    sessionResultData = com.learnit.app.presentation.screen.SessionResult(correct, total, "3m 45s", 8, 12, 1250, 1800, 450)
                                    currentScreen = Screen.Result
                                }
                            )
                            Screen.Result -> {
                                val result = sessionResultData
                                if (result != null) {
                                    SessionResultScreen(
                                        result = result,
                                        onBackToDecks = { currentScreen = Screen.Study },
                                        onViewLeaderboard = { currentScreen = Screen.Leaderboard },
                                        onHomeClick = { currentScreen = Screen.Dashboard },
                                        onFlashcardsClick = { currentScreen = Screen.GenerateFlashcard }
                                    )
                                }
                            }
                            Screen.GenerateFlashcard -> GenerateFlashcardScreen(
                                showBack = previousScreenForFlashcard != null,
                                onBackClick = { 
                                    previousScreenForFlashcard?.let { currentScreen = it }
                                    previousScreenForFlashcard = null
                                },
                                onHomeClick = { 
                                    previousScreenForFlashcard = null
                                    currentScreen = Screen.Dashboard 
                                },
                                onStudyClick = { 
                                    previousScreenForFlashcard = null
                                    currentScreen = Screen.Study 
                                },
                                onLeaderboardClick = { 
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.Leaderboard 
                                },
                                onNotificationClick = { 
                                    previousScreenForNotification = Screen.GenerateFlashcard
                                    currentScreen = Screen.Notification 
                                },
                                onProfileClick = { 
                                    previousScreenForProfile = Screen.GenerateFlashcard
                                    currentScreen = Screen.Profile 
                                },
                                onGenerateSuccess = { count -> 
                                    selectedCardCount = count
                                    previousScreenForStudySession = Screen.GenerateFlashcard
                                    currentScreen = Screen.StudySession 
                                }
                            )
                            Screen.Leaderboard -> LeaderboardScreen(
                                showBack = previousScreenForLeaderboard != null,
                                onBackClick = {
                                    previousScreenForLeaderboard?.let { currentScreen = it }
                                    previousScreenForLeaderboard = null
                                },
                                onHomeClick = { 
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.Dashboard 
                                },
                                onFlashcardsClick = { 
                                    previousScreenForFlashcard = null
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onStudyClick = { 
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.Study 
                                },
                                onNotificationClick = { 
                                    previousScreenForNotification = Screen.Leaderboard
                                    currentScreen = Screen.Notification 
                                },
                                onProfileClick = { 
                                    previousScreenForProfile = Screen.Leaderboard
                                    currentScreen = Screen.Profile 
                                }
                            )
                            Screen.Notification -> NotificationScreen(
                                onBackClick = { 
                                    previousScreenForNotification?.let { currentScreen = it }
                                    previousScreenForNotification = null
                                },
                                onHomeClick = { 
                                    previousScreenForNotification = null
                                    currentScreen = Screen.Dashboard 
                                },
                                onFlashcardsClick = { 
                                    previousScreenForFlashcard = null
                                    previousScreenForNotification = null
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onStudyClick = { 
                                    previousScreenForStudy = null
                                    previousScreenForNotification = null
                                    currentScreen = Screen.Study 
                                },
                                onLeaderboardClick = { 
                                    previousScreenForLeaderboard = null
                                    previousScreenForNotification = null
                                    currentScreen = Screen.Leaderboard 
                                }
                            )
                            Screen.Profile -> ProfileScreen(
                                onBackClick = {
                                    previousScreenForProfile?.let { currentScreen = it }
                                    previousScreenForProfile = null
                                },
                                onHomeClick = { 
                                    previousScreenForProfile = null
                                    currentScreen = Screen.Dashboard 
                                },
                                onFlashcardsClick = { 
                                    previousScreenForFlashcard = null
                                    previousScreenForProfile = null
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onStudyClick = { 
                                    previousScreenForStudy = null
                                    previousScreenForProfile = null
                                    currentScreen = Screen.Study 
                                },
                                onLeaderboardClick = { 
                                    previousScreenForLeaderboard = null
                                    previousScreenForProfile = null
                                    currentScreen = Screen.Leaderboard 
                                },
                                onNotificationClick = { 
                                    previousScreenForNotification = Screen.Profile
                                    currentScreen = Screen.Notification 
                                },
                                onHelpSupportClick = { currentScreen = Screen.HelpSupport },
                                onSettingsClick = { currentScreen = Screen.Settings },
                                onLogout = { currentScreen = Screen.Login }
                            )
                            Screen.HelpSupport -> HelpSupportScreen(
                                onBackClick = { currentScreen = Screen.Profile },
                                onHomeClick = { currentScreen = Screen.Dashboard },
                                onFlashcardsClick = { 
                                    previousScreenForFlashcard = null
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onStudyClick = { currentScreen = Screen.Study },
                                onLeaderboardClick = { 
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.Leaderboard 
                                },
                                onProfileClick = { 
                                    previousScreenForProfile = Screen.HelpSupport
                                    currentScreen = Screen.Profile 
                                },
                                onAppFeaturesClick = { currentScreen = Screen.AppFeatures }
                            )
                            Screen.AppFeatures -> AppFeaturesScreen(
                                onBackClick = { currentScreen = Screen.HelpSupport },
                                onStartCreatingClick = {
                                    previousScreenForFlashcard = Screen.AppFeatures
                                    currentScreen = Screen.GenerateFlashcard
                                }
                            )
                            Screen.Settings -> SettingsScreen(
                                onBackClick = { currentScreen = Screen.Profile },
                                onHomeClick = { currentScreen = Screen.Dashboard },
                                onFlashcardsClick = { currentScreen = Screen.GenerateFlashcard },
                                onStudyClick = { currentScreen = Screen.Study },
                                onLeaderboardClick = { currentScreen = Screen.Leaderboard }
                            )
                        }
                    }

                    if (currentScreen == Screen.Splash || currentScreen == Screen.Login) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = BiasAlignment(0f, verticalBias)
                        ) {
                            Card(
                                modifier = Modifier
                                    .size(cardSize)
                                    .graphicsLayer { alpha = if (isSplash) splashLogoAlpha else 1f }
                                    .shadow(
                                        elevation = cardElevation,
                                        shape = RoundedCornerShape(20.dp),
                                        spotColor = Color(0xFF5E5CE6).copy(alpha = 0.3f)
                                    ),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = cardAlpha)
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    AppLogo(
                                        logoSize = logoSize,
                                        showShadow = isSplash
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun lerpDp(start: Dp, stop: Dp, fraction: Float): Dp {
    return (start.value + (stop.value - start.value) * fraction).dp
}

fun lerpFloat(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}
