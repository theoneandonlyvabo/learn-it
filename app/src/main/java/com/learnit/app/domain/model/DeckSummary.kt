package com.learnit.app.domain.model

data class DeckSummary(
    val deckId: String,
    val title: String,
    val cardCount: Int,
    val lastStudied: String
)

data class DashboardUiState(
    val flashcardCount: Int = 0,
    val learningScore: Int = 0,
    val decks: List<DeckSummary> = emptyList()
)
