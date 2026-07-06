package com.learnit.app.ui.viewmodel

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

            DashboardUiState(
                flashcardCount = cards.size,
                learningScore = sessions.sumOf { it.score },
                decks = decks
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    suspend fun imageUrlFor(topic: String): String? = deckImages.imageUrlFor(topic)

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
