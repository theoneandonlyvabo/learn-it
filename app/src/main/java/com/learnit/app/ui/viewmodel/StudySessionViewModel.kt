package com.learnit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnit.app.data.local.dao.StudySessionDao
import com.learnit.app.data.local.entity.StudySessionEntity
import com.learnit.app.data.repository.AuthRepository
import com.learnit.app.data.repository.LeaderboardRepository
import com.learnit.app.domain.model.Flashcard
import com.learnit.app.domain.model.ScoreCalculator
import com.learnit.app.domain.model.SessionStatus
import com.learnit.app.domain.model.SessionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudySessionViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val authRepository: AuthRepository,
    private val sessionDao: StudySessionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var currentDeckId: String = ""
    private var sessionDuration: Int = 0

    fun startSession(cards: List<Flashcard>, durationSeconds: Int) {
        currentDeckId = cards.firstOrNull()?.deckId ?: ""
        sessionDuration = durationSeconds
        _uiState.value = SessionUiState(
            cards = cards,
            timeRemaining = durationSeconds,
            status = SessionStatus.RUNNING
        )
        launchTimer(durationSeconds)
    }

    private fun launchTimer(durationSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = durationSeconds
            while (remaining > 0 && isActive) {
                delay(1_000)
                remaining--
                _uiState.update { it.copy(timeRemaining = remaining) }
            }
            // Timer expired naturally — finish with whatever was revealed
            if (isActive) finishSession()
        }
    }

    // Each flip hidden→revealed increments revealedCount; flipping back does not decrement.
    fun flipCard() {
        val state = _uiState.value
        if (state.status != SessionStatus.RUNNING) return
        val nowFlipped = !state.isFlipped
        val newRevealed = if (nowFlipped) state.revealedCount + 1 else state.revealedCount
        _uiState.update { it.copy(isFlipped = nowFlipped, revealedCount = newRevealed) }
    }

    fun nextCard() {
        val state = _uiState.value
        if (state.isLastCard) return
        _uiState.update { it.copy(currentCardIndex = it.currentCardIndex + 1, isFlipped = false) }
    }

    fun finishSession() {
        val state = _uiState.value
        if (state.status != SessionStatus.RUNNING) return
        timerJob?.cancel()
        val finalScore = ScoreCalculator.calculate(
            revealedCards = state.revealedCount,
            totalCards = state.cards.size,
            timeRemaining = state.timeRemaining
        )
        _uiState.update { it.copy(score = finalScore, status = SessionStatus.FINISHED) }
        persistSession(finalScore, state.timeRemaining)
        uploadScore(finalScore)
    }

    private fun persistSession(score: Int, timeRemaining: Int) {
        viewModelScope.launch {
            runCatching {
                sessionDao.insertSession(
                    StudySessionEntity(
                        deckId = currentDeckId,
                        score = score,
                        durationSeconds = (sessionDuration - timeRemaining).coerceAtLeast(0)
                    )
                )
            }
        }
    }

    private fun uploadScore(score: Int) {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            runCatching {
                leaderboardRepository.uploadScore(
                    userId = user.uid,
                    username = user.displayName?.takeIf { it.isNotBlank() }
                        ?: user.email?.substringBefore("@") ?: user.uid,
                    score = score
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
