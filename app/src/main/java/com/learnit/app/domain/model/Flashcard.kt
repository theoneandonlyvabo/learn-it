package com.learnit.app.domain.model

data class Flashcard(
    val id: Int = 0,
    val question: String,
    val answer: String,
    val topic: String,
    val deckId: String = ""
)
