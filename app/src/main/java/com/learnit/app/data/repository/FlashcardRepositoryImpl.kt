package com.learnit.app.data.repository

import com.learnit.app.BuildConfig
import com.learnit.app.data.local.dao.FlashcardDao
import com.learnit.app.data.local.entity.FlashcardEntity
import com.learnit.app.data.remote.GlmApiService
import com.learnit.app.data.remote.GlmResponseParser
import com.learnit.app.data.remote.NoConnectivityException
import com.learnit.app.data.remote.dto.GlmMessage
import com.learnit.app.data.remote.dto.GlmRequest
import com.learnit.app.domain.model.Flashcard
import com.learnit.app.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FlashcardRepositoryImpl @Inject constructor(
    private val apiService: GlmApiService,
    private val parser: GlmResponseParser,
    private val dao: FlashcardDao,
    private val networkMonitor: NetworkMonitor
) : FlashcardRepository {

    override suspend fun generateFlashcards(topic: String): Result<List<Flashcard>> {
        if (!networkMonitor.isOnline()) return Result.failure(NoConnectivityException())
        return runCatching {
            val request = GlmRequest(
                model = "glm-4-flash",
                messages = listOf(
                    GlmMessage(role = "system", content = GlmApiService.SYSTEM_PROMPT),
                    GlmMessage(role = "user", content = topic)
                )
            )
            val response = apiService.generateFlashcards(
                token = "Bearer ${BuildConfig.GLM_API_KEY}",
                request = request
            )
            parser.parse(response, topic)
        }
    }

    override suspend fun saveFlashcards(cards: List<Flashcard>, deckId: String) {
        val entities = cards.map { card ->
            FlashcardEntity(
                question = card.question,
                answer = card.answer,
                topic = card.topic,
                deckId = deckId,
                createdAt = System.currentTimeMillis()
            )
        }
        dao.insertFlashcards(entities)
    }

    override fun getFlashcards(): Flow<List<Flashcard>> =
        dao.getAllFlashcards().map { entities ->
            entities.map { e ->
                Flashcard(id = e.id, question = e.question, answer = e.answer, topic = e.topic, deckId = e.deckId)
            }
        }

    override suspend fun deleteFlashcard(id: Int) = dao.deleteById(id)
}
