package com.learnit.app.data.remote

import com.learnit.app.data.remote.dto.FlashcardItem
import com.learnit.app.data.remote.dto.GlmResponse
import com.learnit.app.domain.model.Flashcard
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlmResponseParser @Inject constructor() {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(response: GlmResponse, topic: String): List<Flashcard> {
        val raw = response.choices.firstOrNull()?.message?.content
            ?: throw FlashcardParseException("GLM returned empty content")

        val cleaned = stripFences(raw)

        val items = try {
            json.decodeFromString<List<FlashcardItem>>(cleaned)
        } catch (e: Exception) {
            throw FlashcardParseException("Failed to parse flashcard JSON: ${e.message}", e)
        }

        val valid = items.filter { it.question.isNotBlank() && it.answer.isNotBlank() }
        if (valid.isEmpty()) throw FlashcardParseException("No valid flashcard items in response")

        return valid.map { Flashcard(question = it.question, answer = it.answer, topic = topic) }
    }

    // Models sometimes wrap output in ```json ... ``` despite being instructed otherwise.
    private fun stripFences(raw: String): String {
        val trimmed = raw.trim()
        if (!trimmed.startsWith("```")) return trimmed
        val lines = trimmed.lines()
        return lines.drop(1).dropLastWhile { it.trim() == "```" }.joinToString("\n").trim()
    }
}
