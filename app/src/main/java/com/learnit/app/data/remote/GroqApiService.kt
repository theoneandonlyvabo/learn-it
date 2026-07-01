package com.learnit.app.data.remote

import com.learnit.app.data.remote.dto.GroqRequest
import com.learnit.app.data.remote.dto.GroqResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApiService {
    @POST("chat/completions")
    suspend fun generateFlashcards(
        @Header("Authorization") token: String,
        @Body request: GroqRequest
    ): GroqResponse

    companion object {
        const val SYSTEM_PROMPT =
            "Kamu adalah generator flashcard. Berdasarkan topik yang diberikan user, hasilkan " +
            "8-10 pasangan pertanyaan-jawaban dalam format JSON array murni: " +
            "[{\"question\": \"...\", \"answer\": \"...\"}]. Jangan tambahkan teks lain, " +
            "penjelasan, atau markdown code fence. Hanya JSON valid."

        // llama-3.3-70b-versatile: best free Groq model for instruction-following JSON output
        const val MODEL = "llama-3.3-70b-versatile"
    }
}
