package com.learnit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.learnit.app.data.local.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY createdAt DESC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY createdAt ASC")
    fun getFlashcardsByDeck(deckId: String): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(cards: List<FlashcardEntity>)

    @Delete
    suspend fun deleteFlashcard(card: FlashcardEntity)

    @Update
    suspend fun updateFlashcard(card: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteById(id: Int)
}
