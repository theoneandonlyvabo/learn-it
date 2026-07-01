package com.learnit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnit.app.data.repository.FlashcardRepository
import com.learnit.app.domain.model.Flashcard
import com.learnit.app.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: FlashcardRepository
) : ViewModel() {

    private val _generateState = MutableStateFlow<ApiState<List<Flashcard>>>(ApiState.Loading)
    val generateState: StateFlow<ApiState<List<Flashcard>>> = _generateState.asStateFlow()

    val flashcards: StateFlow<List<Flashcard>> = repository.getFlashcards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun generate(topic: String) {
        if (topic.isBlank()) {
            _generateState.value = ApiState.Error("Topic cannot be empty")
            return
        }
        viewModelScope.launch {
            _generateState.value = ApiState.Loading
            repository.generateFlashcards(topic).fold(
                onSuccess = { cards ->
                    val deckId = "${topic.trim().take(50)}_${System.currentTimeMillis()}"
                    repository.saveFlashcards(cards, deckId)
                    _generateState.value = ApiState.Success(cards)
                },
                onFailure = { e ->
                    _generateState.value = ApiState.Error(e.message ?: "Unknown error")
                }
            )
        }
    }

    fun deleteFlashcard(id: Int) {
        viewModelScope.launch { repository.deleteFlashcard(id) }
    }
}
