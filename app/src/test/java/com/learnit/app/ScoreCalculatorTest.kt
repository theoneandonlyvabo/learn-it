package com.learnit.app

import com.learnit.app.domain.model.ScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreCalculatorTest {

    @Test
    fun `partial reveals no completion bonus`() {
        // 3 of 10 cards revealed, time left — not all revealed so no bonus
        assertEquals(30, ScoreCalculator.calculate(revealedCards = 3, totalCards = 10, timeRemaining = 60))
    }

    @Test
    fun `all revealed with time remaining gives bonus`() {
        // 10 * 10 = 100 base + 45 time bonus
        assertEquals(145, ScoreCalculator.calculate(revealedCards = 10, totalCards = 10, timeRemaining = 45))
    }

    @Test
    fun `all revealed but timer expired gives no bonus`() {
        assertEquals(100, ScoreCalculator.calculate(revealedCards = 10, totalCards = 10, timeRemaining = 0))
    }

    @Test
    fun `zero reveals gives zero score`() {
        assertEquals(0, ScoreCalculator.calculate(revealedCards = 0, totalCards = 10, timeRemaining = 30))
    }
}
