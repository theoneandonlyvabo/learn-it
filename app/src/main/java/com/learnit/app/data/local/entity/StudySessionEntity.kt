package com.learnit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: String,
    val score: Int,
    val durationSeconds: Int,
    val completedAt: Long = System.currentTimeMillis()
)
