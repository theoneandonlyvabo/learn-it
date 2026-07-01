package com.learnit.app.data.repository

import com.learnit.app.domain.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    suspend fun uploadScore(userId: String, username: String, score: Int)
    fun getLeaderboard(): Flow<List<LeaderboardEntry>>
}
