package com.learnit.app

import com.google.firebase.auth.FirebaseUser
import com.learnit.app.data.repository.AuthRepository
import com.learnit.app.data.repository.LeaderboardRepository
import com.learnit.app.domain.model.Flashcard
import com.learnit.app.domain.model.LeaderboardEntry
import com.learnit.app.domain.model.SessionStatus
import com.learnit.app.ui.viewmodel.StudySessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StudySessionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun makeVm() = StudySessionViewModel(
        leaderboardRepository = object : LeaderboardRepository {
            override suspend fun uploadScore(userId: String, username: String, score: Int) {}
            override fun getLeaderboard(): Flow<List<LeaderboardEntry>> = emptyFlow()
        },
        authRepository = object : AuthRepository {
            override suspend fun register(email: String, password: String): Result<FirebaseUser> =
                Result.failure(Exception("fake"))
            override suspend fun login(email: String, password: String): Result<FirebaseUser> =
                Result.failure(Exception("fake"))
            override fun getCurrentUser(): FirebaseUser? = null
            override fun logout() {}
        }
    )

    private val sampleCards = listOf(
        Flashcard(id = 1, question = "Q1", answer = "A1", topic = "Test"),
        Flashcard(id = 2, question = "Q2", answer = "A2", topic = "Test"),
        Flashcard(id = 3, question = "Q3", answer = "A3", topic = "Test"),
    )

    @Test
    fun `startSession sets initial state`() {
        val vm = makeVm()
        vm.startSession(sampleCards, durationSeconds = 60)
        val state = vm.uiState.value
        assertEquals(sampleCards, state.cards)
        assertEquals(60, state.timeRemaining)
        assertEquals(0, state.currentCardIndex)
        assertEquals(SessionStatus.RUNNING, state.status)
    }

    @Test
    fun `flipCard increments revealedCount and sets isFlipped`() {
        val vm = makeVm()
        vm.startSession(sampleCards, 60)
        vm.flipCard()
        assertEquals(1, vm.uiState.value.revealedCount)
        assertEquals(true, vm.uiState.value.isFlipped)
    }

    @Test
    fun `flip back does not decrement revealedCount`() {
        val vm = makeVm()
        vm.startSession(sampleCards, 60)
        vm.flipCard()  // reveal
        vm.flipCard()  // hide
        assertEquals(1, vm.uiState.value.revealedCount)
        assertEquals(false, vm.uiState.value.isFlipped)
    }

    @Test
    fun `nextCard advances index and resets flip state`() {
        val vm = makeVm()
        vm.startSession(sampleCards, 60)
        vm.flipCard()
        vm.nextCard()
        assertEquals(1, vm.uiState.value.currentCardIndex)
        assertEquals(false, vm.uiState.value.isFlipped)
    }

    @Test
    fun `finishSession with partial reveals scores base only`() {
        val vm = makeVm()
        vm.startSession(sampleCards, 60)
        vm.flipCard()  // reveal card 0 only
        vm.finishSession()
        val state = vm.uiState.value
        // 1 revealed * 10 = 10; not all cards so no bonus
        assertEquals(10, state.score)
        assertEquals(SessionStatus.FINISHED, state.status)
    }

    @Test
    fun `finishSession with all cards revealed and time remaining adds bonus`() {
        val vm = makeVm()
        vm.startSession(sampleCards, 60)
        // Reveal all 3 cards (timer hasn't advanced, timeRemaining stays 60)
        vm.flipCard(); vm.nextCard()
        vm.flipCard(); vm.nextCard()
        vm.flipCard()
        vm.finishSession()
        val state = vm.uiState.value
        // 3 * 10 + 60 bonus = 90
        assertEquals(90, state.score)
        assertEquals(SessionStatus.FINISHED, state.status)
    }

    @Test
    fun `status is FINISHED after finishSession`() {
        val vm = makeVm()
        vm.startSession(sampleCards, 30)
        vm.finishSession()
        assertEquals(SessionStatus.FINISHED, vm.uiState.value.status)
    }
}
