package com.learnit.app.domain.model

data class DeckSummary(
    val deckId: String,
    val title: String,
    val cardCount: Int,
    val lastStudied: String,
    val imageRes: Int? = null
)

data class DashboardUiState(
    val flashcardCount: Int = 0,
    val learningScore: Int = 0,
    val totalStudyTimeSeconds: Long = 0,
    val daysStreak: Int = 0,
    val cardsMastered: Int = 0,
    val achievements: List<com.learnit.app.presentation.screen.Achievement> = emptyList(),
    val decks: List<DeckSummary> = emptyList()
)
