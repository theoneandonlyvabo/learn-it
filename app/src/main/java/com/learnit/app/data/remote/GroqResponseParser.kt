package com.learnit.app.data.remote

import com.learnit.app.data.remote.dto.FlashcardGenerationContainer
import com.learnit.app.data.remote.dto.FlashcardItem
import com.learnit.app.data.remote.dto.GroqResponse
import com.learnit.app.domain.model.Flashcard
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqResponseParser @Inject constructor() {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(response: GroqResponse, originalTopic: String): List<Flashcard> {
        val raw = response.choices.firstOrNull()?.message?.content
            ?: throw FlashcardParseException("Groq returned empty content")

        val cleaned = stripFences(raw)

        return try {
            val container = json.decodeFromString<FlashcardGenerationContainer>(cleaned)
            val valid = container.cards.filter { it.question.isNotBlank() && it.answer.isNotBlank() }
            if (valid.isEmpty()) throw FlashcardParseException("No valid flashcard items in response")
            valid.map { Flashcard(question = it.question, answer = it.answer, topic = container.title) }
        } catch (e: Exception) {
            // Fallback to list format if container format fails
            try {
                val items = json.decodeFromString<List<FlashcardItem>>(cleaned)
                val valid = items.filter { it.question.isNotBlank() && it.answer.isNotBlank() }
                if (valid.isEmpty()) throw FlashcardParseException("No valid flashcard items in fallback")
                
                val conciseTitle = if (originalTopic.length > 40) originalTopic.take(37) + "..." else originalTopic
                valid.map { Flashcard(question = it.question, answer = it.answer, topic = conciseTitle) }
            } catch (inner: Exception) {
                throw FlashcardParseException("Failed to parse flashcard JSON: ${inner.message}", inner)
            }
        }
    }

    private fun stripFences(raw: String): String {
        val trimmed = raw.trim()
        if (!trimmed.startsWith("```")) return trimmed
        val lines = trimmed.lines()
        return lines.drop(1).dropLastWhile { it.trim() == "```" }.joinToString("\n").trim()
    }
}
