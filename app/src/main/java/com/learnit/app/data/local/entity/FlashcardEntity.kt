package com.learnit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val question: String,
    val answer: String,
    val createdAt: Long = System.currentTimeMillis(),
    val deckId: String
)
