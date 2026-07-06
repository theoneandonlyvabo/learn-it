package com.learnit.app.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Whatshot
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnit.app.data.local.dao.StudySessionDao
import com.learnit.app.data.remote.DeckImageProvider
import com.learnit.app.data.repository.FlashcardRepository
import com.learnit.app.domain.model.DashboardUiState
import com.learnit.app.domain.model.DeckSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    flashcardRepository: FlashcardRepository,
    sessionDao: StudySessionDao,
    private val deckImages: DeckImageProvider
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        combine(
            flashcardRepository.getFlashcards(),
            sessionDao.getAllSessions()
        ) { cards, sessions ->
            val lastStudiedByDeck = sessions.groupBy { it.deckId }
                .mapValues { (_, s) -> s.maxOf { it.completedAt } }

            // `cards` arrives newest-first (DAO orders by createdAt DESC), so groupBy keeps the
            // most-recently-generated deck first. sortedByDescending is stable, so studied decks
            // bubble to the top by recency and unstudied decks keep newest-generated-first order.
            val decks = cards
                .filter { it.deckId.isNotBlank() }
                .groupBy { it.deckId }
                .map { (deckId, deckCards) ->
                    val studiedAt = lastStudiedByDeck[deckId]
                    DeckSummary(
                        deckId = deckId,
                        title = deckCards.first().topic,
                        cardCount = deckCards.size,
                        lastStudied = if (studiedAt != null) "Last studied ${relativeAgo(studiedAt)}" else "Not studied yet"
                    )
                }
                .sortedByDescending { lastStudiedByDeck[it.deckId] ?: Long.MIN_VALUE }

            val studiedDeckIds = sessions.map { it.deckId }.toSet()
            val cardsMastered = cards.filter { it.deckId in studiedDeckIds }.size

            DashboardUiState(
                flashcardCount = cards.size,
                learningScore = sessions.sumOf { it.score },
                totalStudyTimeSeconds = sessions.sumOf { it.durationSeconds.toLong() },
                daysStreak = calculateStreak(sessions),
                cardsMastered = cardsMastered,
                achievements = generateAchievements(cards.size, sessions.sumOf { it.score }, calculateStreak(sessions)),
                decks = decks
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    private fun generateAchievements(flashcardCount: Int, learningScore: Int, streak: Int): List<com.learnit.app.presentation.screen.Achievement> {
        val list = mutableListOf<com.learnit.app.presentation.screen.Achievement>()
        val icons = Icons.Default
        
        if (flashcardCount >= 1) {
            list.add(com.learnit.app.presentation.screen.Achievement("First Deck", icons.Add))
        }
        if (streak >= 3) {
            list.add(com.learnit.app.presentation.screen.Achievement("Consistent Learner", icons.Person))
        }
        if (streak >= 7) {
            list.add(com.learnit.app.presentation.screen.Achievement("7-Day Streak", icons.Whatshot))
        }
        if (learningScore >= 1000) {
            list.add(com.learnit.app.presentation.screen.Achievement("Knowledge Seeker", icons.Home))
        }
        if (learningScore >= 5000) {
            list.add(com.learnit.app.presentation.screen.Achievement("Master Mind", icons.BarChart))
        }
        
        return list
    }

    suspend fun imageUrlFor(topic: String): String? = deckImages.imageUrlFor(topic)

    private fun calculateStreak(sessions: List<com.learnit.app.data.local.entity.StudySessionEntity>): Int {
        if (sessions.isEmpty()) return 0
        
        val calendar = java.util.Calendar.getInstance()
        val sessionDates = sessions.map {
            calendar.timeInMillis = it.completedAt
            val y = calendar.get(java.util.Calendar.YEAR)
            val d = calendar.get(java.util.Calendar.DAY_OF_YEAR)
            y * 1000 + d
        }.distinct().sortedDescending()

        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar.get(java.util.Calendar.YEAR) * 1000 + calendar.get(java.util.Calendar.DAY_OF_YEAR)
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.get(java.util.Calendar.YEAR) * 1000 + calendar.get(java.util.Calendar.DAY_OF_YEAR)

        if (sessionDates.first() != today && sessionDates.first() != yesterday) {
            return 0
        }

        var streak = 0
        val checkCalendar = java.util.Calendar.getInstance()
        if (sessionDates.first() == today) {
            checkCalendar.timeInMillis = System.currentTimeMillis()
        } else {
            checkCalendar.timeInMillis = System.currentTimeMillis()
            checkCalendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }

        val dateSet = sessionDates.toSet()
        while (true) {
            val key = checkCalendar.get(java.util.Calendar.YEAR) * 1000 + checkCalendar.get(java.util.Calendar.DAY_OF_YEAR)
            if (dateSet.contains(key)) {
                streak++
                checkCalendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    private fun relativeAgo(millis: Long): String {
        val diff = System.currentTimeMillis() - millis
        val minutes = diff / 60_000
        val hours = minutes / 60
        val days = hours / 24
        return when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days == 1L -> "yesterday"
            days < 7 -> "$days days ago"
            else -> "${days / 7}w ago"
        }
    }
}
