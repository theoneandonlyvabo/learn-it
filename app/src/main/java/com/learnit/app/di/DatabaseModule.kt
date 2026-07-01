package com.learnit.app.di

import android.content.Context
import androidx.room.Room
import com.learnit.app.data.local.LearnitDatabase
import com.learnit.app.data.local.dao.FlashcardDao
import com.learnit.app.data.local.dao.StudySessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LearnitDatabase =
        Room.databaseBuilder(context, LearnitDatabase::class.java, "learnit.db").build()

    @Provides
    fun provideFlashcardDao(db: LearnitDatabase): FlashcardDao = db.flashcardDao()

    @Provides
    fun provideStudySessionDao(db: LearnitDatabase): StudySessionDao = db.studySessionDao()
}
