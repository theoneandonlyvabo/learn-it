package com.learnit.app.data.repository

import com.learnit.app.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {
    suspend fun generateFlashcards(topic: String): Result<List<Flashcard>>
    suspend fun saveFlashcards(cards: List<Flashcard>, deckId: String)
    fun getFlashcards(): Flow<List<Flashcard>>
    suspend fun deleteFlashcard(id: Int)
}
