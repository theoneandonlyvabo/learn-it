package com.learnit.app

import com.google.firebase.auth.FirebaseUser
import com.learnit.app.data.repository.AuthRepository
import com.learnit.app.data.repository.LeaderboardRepository
import com.learnit.app.domain.model.LeaderboardEntry
import com.learnit.app.ui.viewmodel.LeaderboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private class FakeLeaderboardRepository(
        private val entries: List<LeaderboardEntry>
    ) : LeaderboardRepository {
        override suspend fun uploadScore(userId: String, username: String, score: Int) {}
        override fun getLeaderboard(): Flow<List<LeaderboardEntry>> = flowOf(entries)
    }

    private class FakeAuthRepository(private val user: FirebaseUser?) : AuthRepository {
        override suspend fun register(email: String, password: String, displayName: String): Result<FirebaseUser> =
            Result.failure(Exception("fake"))
        override suspend fun login(email: String, password: String): Result<FirebaseUser> =
            Result.failure(Exception("fake"))
        override fun getCurrentUser(): FirebaseUser? = user
        override fun logout() {}
    }

    private val sampleEntries = listOf(
        LeaderboardEntry(userId = "u1", username = "Alice", score = 100),
        LeaderboardEntry(userId = "u2", username = "Bob", score = 90),
    )

    @Test
    fun `entries reflects repository leaderboard`() = runTest {
        val vm = LeaderboardViewModel(
            FakeLeaderboardRepository(sampleEntries),
            FakeAuthRepository(user = null)
        )
        // entries is WhileSubscribed — must collect to trigger upstream emission.
        assertEquals(sampleEntries, vm.entries.first { it.isNotEmpty() })
    }

    @Test
    fun `currentUserId is null when no user is logged in`() {
        val vm = LeaderboardViewModel(
            FakeLeaderboardRepository(sampleEntries),
            FakeAuthRepository(user = null)
        )
        assertNull(vm.currentUserId)
    }
}
