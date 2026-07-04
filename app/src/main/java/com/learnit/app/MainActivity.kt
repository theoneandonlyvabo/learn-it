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
    Splash, Login, Register, Dashboard, Study, StudySession, SessionResult, 
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
                var selectedCardCount by remember { mutableIntStateOf(5) }
                var sessionResultData by remember { mutableStateOf<SessionResult?>(null) }
                
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
                    animationSpec = tween(durationMillis = 1000, easing = EaseInOutCubic),
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
                            val isNavigatingToMain = targetState in listOf(
                                Screen.Dashboard, Screen.Study, Screen.GenerateFlashcard, 
                                Screen.Leaderboard, Screen.SessionResult, Screen.Notification, 
                                Screen.Profile, Screen.HelpSupport, Screen.AppFeatures
                            )
                            if (isNavigatingToMain) {
                                (fadeIn(animationSpec = tween(800)) + scaleIn(initialScale = 0.95f))
                                    .togetherWith(fadeOut(animationSpec = tween(400)))
                            } else if (targetState == Screen.Register || initialState == Screen.Register) {
                                slideInHorizontally { if (targetState == Screen.Register) it else -it } + fadeIn() togetherWith
                                slideOutHorizontally { if (targetState == Screen.Register) -it else it } + fadeOut()
                            } else {
                                fadeIn(animationSpec = tween(800)) togetherWith fadeOut(animationSpec = tween(500))
                            }
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
                                onNotificationClick = { currentScreen = Screen.Notification },
                                onProfileClick = { currentScreen = Screen.Profile },
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
                                onNotificationClick = { currentScreen = Screen.Notification },
                                onProfileClick = { currentScreen = Screen.Profile },
                                onCreateDeckClick = { 
                                    previousScreenForFlashcard = Screen.Study
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onStudyNowClick = { _ -> 
                                    previousScreenForStudySession = Screen.Study
                                    currentScreen = Screen.StudySession 
                                },
                                onViewResultClick = { _ -> 
                                    sessionResultData = SessionResult(50, 50, "5m 20s", 12, 15, 1500, 2000, 500)
                                    currentScreen = Screen.SessionResult
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
                                    sessionResultData = SessionResult(correct, total, "3m 45s", 8, 12, 1250, 1800, 450)
                                    currentScreen = Screen.SessionResult
                                }
                            )
                            Screen.SessionResult -> {
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
                                onNotificationClick = { currentScreen = Screen.Notification },
                                onProfileClick = { currentScreen = Screen.Profile },
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
                                onNotificationClick = { currentScreen = Screen.Notification },
                                onProfileClick = { currentScreen = Screen.Profile }
                            )
                            Screen.Notification -> NotificationScreen(
                                onBackClick = { currentScreen = Screen.Dashboard },
                                onHomeClick = { currentScreen = Screen.Dashboard },
                                onFlashcardsClick = { 
                                    previousScreenForFlashcard = null
                                    currentScreen = Screen.GenerateFlashcard 
                                },
                                onStudyClick = { currentScreen = Screen.Study },
                                onLeaderboardClick = { 
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.Leaderboard 
                                }
                            )
                            Screen.Profile -> ProfileScreen(
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
                                onNotificationClick = { currentScreen = Screen.Notification },
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
                                onProfileClick = { currentScreen = Screen.Profile },
                                onAppFeaturesClick = { currentScreen = Screen.AppFeatures }
                            )
                            Screen.AppFeatures -> AppFeaturesScreen(
                                onBackClick = { currentScreen = Screen.HelpSupport }
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
