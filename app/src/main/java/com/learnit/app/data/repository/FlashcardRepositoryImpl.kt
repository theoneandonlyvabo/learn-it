package com.learnit.app.data.repository

import com.learnit.app.BuildConfig
import com.learnit.app.data.local.dao.FlashcardDao
import com.learnit.app.data.local.entity.FlashcardEntity
import com.learnit.app.data.remote.GroqApiService
import com.learnit.app.data.remote.GroqResponseParser
import com.learnit.app.data.remote.NoConnectivityException
import com.learnit.app.data.remote.dto.GroqMessage
import com.learnit.app.data.remote.dto.GroqRequest
import com.learnit.app.domain.model.Flashcard
import com.learnit.app.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FlashcardRepositoryImpl @Inject constructor(
    private val apiService: GroqApiService,
    private val parser: GroqResponseParser,
    private val dao: FlashcardDao,
    private val networkMonitor: NetworkMonitor
) : FlashcardRepository {

    override suspend fun generateFlashcards(topic: String, category: String, count: Int): Result<List<Flashcard>> {
        if (!networkMonitor.isOnline()) return Result.failure(NoConnectivityException())
        return runCatching {
            val systemPrompt = "Kamu adalah generator flashcard ahli dalam bidang $category. " +
                    "Berdasarkan topik atau teks yang diberikan user, hasilkan " +
                    "judul yang singkat (maksimal 5 kata) dan tepat untuk deck tersebut, serta " +
                    "$count pasangan pertanyaan-jawaban.\n\n" +
                    "Format output WAJIB JSON murni seperti ini:\n" +
                    "{\n" +
                    "  \"title\": \"Judul Singkat\",\n" +
                    "  \"cards\": [\n" +
                    "    {\"question\": \"...\", \"answer\": \"...\"}\n" +
                    "  ]\n" +
                    "}\n\n" +
                    "Jangan tambahkan teks lain atau markdown code fence."

            val request = GroqRequest(
                model = GroqApiService.MODEL,
                messages = listOf(
                    GroqMessage(role = "system", content = systemPrompt),
                    GroqMessage(role = "user", content = topic)
                )
            )
            val response = apiService.generateFlashcards(
                token = "Bearer ${BuildConfig.GROQ_API_KEY}",
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
