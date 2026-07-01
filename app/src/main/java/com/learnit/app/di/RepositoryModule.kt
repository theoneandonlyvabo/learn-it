package com.learnit.app.di

import com.learnit.app.data.repository.AuthRepository
import com.learnit.app.data.repository.AuthRepositoryImpl
import com.learnit.app.data.repository.FlashcardRepository
import com.learnit.app.data.repository.FlashcardRepositoryImpl
import com.learnit.app.data.repository.LeaderboardRepository
import com.learnit.app.data.repository.LeaderboardRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFlashcardRepository(impl: FlashcardRepositoryImpl): FlashcardRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(impl: LeaderboardRepositoryImpl): LeaderboardRepository
}
