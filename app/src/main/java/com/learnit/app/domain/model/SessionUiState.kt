package com.learnit.app.domain.model

data class SessionUiState(
    val cards: List<Flashcard> = emptyList(),
    val currentCardIndex: Int = 0,
    val timeElapsed: Int = 0,
    val isFlipped: Boolean = false,
    val score: Int = 0,
    val revealedIndices: Set<Int> = emptySet(),
    val status: SessionStatus = SessionStatus.RUNNING
) {
    val currentCard: Flashcard? get() = cards.getOrNull(currentCardIndex)
    val isLastCard: Boolean get() = currentCardIndex >= cards.size - 1
    val revealedCount: Int get() = revealedIndices.size
}
