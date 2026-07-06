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
import androidx.hilt.navigation.compose.hiltViewModel
import com.learnit.app.presentation.component.AppLogo
import com.learnit.app.presentation.screen.*
import com.learnit.app.presentation.ui.theme.LearnItTheme
import com.learnit.app.ui.viewmodel.AuthViewModel
import com.learnit.app.ui.viewmodel.DashboardViewModel
import com.learnit.app.ui.viewmodel.FlashcardViewModel
import com.learnit.app.ui.viewmodel.LeaderboardViewModel
import com.learnit.app.ui.viewmodel.StudySessionViewModel
import com.learnit.app.util.ApiState
import dagger.hilt.android.AndroidEntryPoint

private const val SESSION_DURATION_SECONDS = 300

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
                var sessionResultData by remember { mutableStateOf<DeckMasteredResult?>(null) }

                val authVm: AuthViewModel = hiltViewModel()
                val flashcardVm: FlashcardViewModel = hiltViewModel()
                val studyVm: StudySessionViewModel = hiltViewModel()
                val leaderboardVm: LeaderboardViewModel = hiltViewModel()
                val dashboardVm: DashboardViewModel = hiltViewModel()

                val authState by authVm.authState.collectAsState()
                val dashboardState by dashboardVm.uiState.collectAsState()
                val flashcards by flashcardVm.flashcards.collectAsState()
                val generateState by flashcardVm.generateState.collectAsState()
                val sessionState by studyVm.uiState.collectAsState()
                val leaderboardEntries by leaderboardVm.entries.collectAsState()

                var hasStartedGenerating by remember { mutableStateOf(false) }

                // Keyed on currentScreen too so an already-authenticated authState (e.g. surviving
                // a config change that reset currentScreen back to Login) still redirects.
                LaunchedEffect(authState, currentScreen) {
                    if (authState is ApiState.Success &&
                        (currentScreen == Screen.Login || currentScreen == Screen.Register)
                    ) {
                        currentScreen = Screen.Dashboard
                    }
                }

                LaunchedEffect(generateState) {
                    val state = generateState
                    if (hasStartedGenerating && state is ApiState.Success) {
                        hasStartedGenerating = false
                        if (state.data.isNotEmpty()) {
                            studyVm.startSession(state.data, SESSION_DURATION_SECONDS)
                            previousScreenForStudySession = Screen.GenerateFlashcard
                            currentScreen = Screen.StudySession
                        }
                    } else if (hasStartedGenerating && state is ApiState.Error) {
                        // Attempt finished (failed) — stop treating the screen as "generating".
                        hasStartedGenerating = false
                    }
                }

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
                            Screen.Login -> {
                                // Only dismiss a stale Error — never clear Success, which the
                                // effect above still needs to see in order to navigate away.
                                LaunchedEffect(Unit) { if (authState !is ApiState.Success) authVm.clearState() }
                                LoginScreen(
                                    logoContent = { Box(Modifier.size(80.dp)) },
                                    onRegisterClick = { currentScreen = Screen.Register },
                                    onLogin = { email, password -> authVm.login(email, password) },
                                    isLoading = authState is ApiState.Loading,
                                    errorMessage = (authState as? ApiState.Error)?.errorMsg
                                )
                            }
                            Screen.Register -> {
                                // Only dismiss a stale Error — never clear Success, which the
                                // effect above still needs to see in order to navigate away.
                                LaunchedEffect(Unit) { if (authState !is ApiState.Success) authVm.clearState() }
                                RegisterScreen(
                                    onBackClick = { currentScreen = Screen.Login },
                                    onSignInClick = { currentScreen = Screen.Login },
                                    onRegister = { name, email, password -> authVm.register(email, password, name) },
                                    isLoading = authState is ApiState.Loading,
                                    errorMessage = (authState as? ApiState.Error)?.errorMsg
                                )
                            }
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
                                    previousScreenForStudy = null
                                    currentScreen = Screen.Study
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
                                },
                                userName = authVm.currentName,
                                flashcardCount = dashboardState.flashcardCount,
                                learningScore = dashboardState.learningScore,
                                decks = dashboardState.decks,
                                onDeckClick = {
                                    previousScreenForStudy = Screen.Dashboard
                                    currentScreen = Screen.Study
                                },
                                resolveDeckImage = dashboardVm::imageUrlFor
                            )
                            Screen.Study -> {
                                val decksList = remember(flashcards) {
                                    flashcards.groupBy { it.deckId }
                                        .filterKeys { it.isNotBlank() }
                                        .map { (deckId, cards) ->
                                            StudyDeck(
                                                deckId = deckId,
                                                title = deckId.substringBeforeLast("_").ifBlank { "Untitled Deck" },
                                                cardCount = cards.size,
                                                lastStudied = "Not studied yet",
                                                progress = 0f
                                            )
                                        }
                                }
                                StudyScreen(
                                    showBack = previousScreenForStudy != null,
                                    userName = authVm.currentName,
                                    onBackClick = {
                                        previousScreenForStudy?.let { currentScreen = it }
                                        previousScreenForStudy = null
                                    },
                                    onHomeClick = {
                                        previousScreenForStudy = null
                                        currentScreen = Screen.Dashboard
                                    },
                                    onFlashcardsClick = { }, // already on the Flashcards tab
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
                                    onStudyNowClick = { deckId ->
                                        val deckCards = flashcards.filter { it.deckId == deckId }
                                        // ponytail: no-op for a deckId with no cards (e.g. the static
                                        // AI-recommendation card, which isn't backed by a real deck).
                                        if (deckCards.isNotEmpty()) {
                                            studyVm.startSession(deckCards, SESSION_DURATION_SECONDS)
                                            previousScreenForStudySession = Screen.Study
                                            currentScreen = Screen.StudySession
                                        }
                                    },
                                    // ponytail: no persisted per-deck history yet, so DeckCard never
                                    // renders isCompleted=true for a real deck and this never fires.
                                    onViewResultClick = { },
                                    decks = decksList
                                )
                            }
                            Screen.StudySession -> StudySessionScreen(
                                state = sessionState,
                                onBackClick = {
                                    previousScreenForStudySession?.let { currentScreen = it }
                                    // Jika tidak ada history, default ke Study
                                    if (previousScreenForStudySession == null) currentScreen = Screen.Study
                                },
                                onFlip = { studyVm.flipCard() },
                                onNext = { studyVm.nextCard() },
                                onFinish = {
                                    studyVm.finishSession()
                                    val finished = studyVm.uiState.value
                                    // ponytail: level/streak/XP-bar gamification is static —
                                    // ScoreCalculator only produces one aggregate score.
                                    sessionResultData = DeckMasteredResult(
                                        totalCards = finished.cards.size,
                                        completionXp = finished.score,
                                        timeBonusXp = 0,
                                        streakXp = 0,
                                        gainedXp = finished.score,
                                        currentLevel = 12,
                                        nextLevel = 13,
                                        currentXp = 1250,
                                        maxXp = 1800
                                    )
                                    currentScreen = Screen.Result
                                }
                            )
                            Screen.Result -> {
                                val resultData = sessionResultData
                                if (resultData != null) {
                                    DeckMasteredScreen(
                                        result = resultData,
                                        onBackClick = { 
                                            previousScreenForStudySession?.let { currentScreen = it }
                                            if (previousScreenForStudySession == null) currentScreen = Screen.Study
                                        },
                                        onBackToDecks = { 
                                            previousScreenForStudy = null
                                            currentScreen = Screen.Study 
                                        },
                                        onLeaderboardClick = { 
                                            previousScreenForLeaderboard = null
                                            currentScreen = Screen.Leaderboard 
                                        },
                                        onHomeClick = { currentScreen = Screen.Dashboard },
                                        onFlashcardsClick = { 
                                            previousScreenForFlashcard = null
                                            currentScreen = Screen.GenerateFlashcard 
                                        }
                                    )
                                }
                            }
                            Screen.GenerateFlashcard -> {
                                // Only dismiss a stale Error — never touch an in-flight attempt.
                                LaunchedEffect(Unit) {
                                    if (!hasStartedGenerating) flashcardVm.resetGenerateState()
                                }
                                GenerateFlashcardScreen(
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
                                    isGenerating = hasStartedGenerating && generateState is ApiState.Loading,
                                    errorMessage = (generateState as? ApiState.Error)?.errorMsg,
                                    onGenerate = { topic ->
                                        hasStartedGenerating = true
                                        flashcardVm.generate(topic)
                                    }
                                )
                            }
                            Screen.Leaderboard -> LeaderboardScreen(
                                showBack = previousScreenForLeaderboard != null,
                                userName = authVm.currentName,
                                onBackClick = {
                                    previousScreenForLeaderboard?.let { currentScreen = it }
                                    previousScreenForLeaderboard = null
                                },
                                onHomeClick = {
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.Dashboard
                                },
                                onFlashcardsClick = {
                                    previousScreenForStudy = null
                                    previousScreenForLeaderboard = null
                                    currentScreen = Screen.Study
                                },
                                onCreateClick = {
                                    previousScreenForFlashcard = Screen.Leaderboard
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
                                },
                                entries = leaderboardEntries,
                                currentUserId = leaderboardVm.currentUserId
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
                                    previousScreenForStudy = null
                                    previousScreenForProfile = null
                                    currentScreen = Screen.Study
                                },
                                onCreateClick = {
                                    previousScreenForFlashcard = Screen.Profile
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
                                onLogout = {
                                    authVm.logout()
                                    currentScreen = Screen.Login
                                },
                                name = authVm.currentName,
                                email = authVm.currentEmail
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
                                onLeaderboardClick = { currentScreen = Screen.Leaderboard },
                                initialName = authVm.currentName,
                                initialEmail = authVm.currentEmail
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
