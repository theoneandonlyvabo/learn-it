package com.learnit.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.learnit.app.domain.model.LeaderboardEntry
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LeaderboardRepository {

    private val collection get() = firestore.collection("leaderboard")

    // Only overwrite if the new score is strictly higher than the stored best.
    override suspend fun uploadScore(userId: String, username: String, score: Int) {
        val ref = collection.document(userId)
        val existing = runCatching { ref.get().await() }.getOrNull()
        val currentBest = existing?.getLong("score")?.toInt() ?: 0
        if (score > currentBest) {
            ref.set(mapOf("userId" to userId, "username" to username, "score" to score)).await()
        }
    }

    override fun getLeaderboard(): Flow<List<LeaderboardEntry>> = callbackFlow {
        val listener = collection
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // ponytail: permission/offline errors degrade to an empty board, never crash the app.
                    // WhileSubscribed re-subscribes and retries once auth/rules allow the read.
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val entries = snapshot?.documents?.mapNotNull { doc ->
                    val userId = doc.getString("userId") ?: return@mapNotNull null
                    val username = doc.getString("username") ?: return@mapNotNull null
                    val docScore = doc.getLong("score")?.toInt() ?: return@mapNotNull null
                    LeaderboardEntry(userId, username, docScore)
                } ?: emptyList()
                trySend(entries)
            }
        awaitClose { listener.remove() }
    }
}
