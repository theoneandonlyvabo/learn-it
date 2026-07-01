package com.learnit.app

import com.learnit.app.data.repository.FlashcardRepository
import com.learnit.app.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeFlashcardRepository : FlashcardRepository {
    private val _cards = MutableStateFlow<List<Flashcard>>(emptyList())
    var generateResult: Result<List<Flashcard>> = Result.success(emptyList())

    override suspend fun generateFlashcards(topic: String): Result<List<Flashcard>> = generateResult

    override suspend fun saveFlashcards(cards: List<Flashcard>, deckId: String) {
        _cards.value = _cards.value + cards.map { it.copy(deckId = deckId) }
    }

    override fun getFlashcards(): Flow<List<Flashcard>> = _cards.asStateFlow()

    override suspend fun deleteFlashcard(id: Int) {
        _cards.value = _cards.value.filter { it.id != id }
    }
}
