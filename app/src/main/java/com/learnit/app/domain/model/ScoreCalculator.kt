package com.learnit.app.domain.model

object ScoreCalculator {
    // +10 per card revealed; bonus = timeRemainingSeconds only if ALL cards revealed before timeout.
    // ponytail: deterministic integer formula, easy to unit-test (Bab IV).
    fun calculate(revealedCards: Int, totalCards: Int, timeRemaining: Int): Int {
        val base = revealedCards * 10
        val bonus = if (revealedCards >= totalCards && timeRemaining > 0) timeRemaining else 0
        return base + bonus
    }
}
