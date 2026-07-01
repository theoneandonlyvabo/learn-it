package com.learnit.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learnit.app.data.local.dao.FlashcardDao
import com.learnit.app.data.local.dao.StudySessionDao
import com.learnit.app.data.local.entity.FlashcardEntity
import com.learnit.app.data.local.entity.StudySessionEntity

@Database(
    entities = [FlashcardEntity::class, StudySessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LearnitDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
    abstract fun studySessionDao(): StudySessionDao
}
