package com.learnit.app.data.remote

import com.learnit.app.data.remote.dto.GlmRequest
import com.learnit.app.data.remote.dto.GlmResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GlmApiService {
    @POST("chat/completions")
    suspend fun generateFlashcards(
        @Header("Authorization") token: String,
        @Body request: GlmRequest
    ): GlmResponse

    companion object {
        // System prompt demands strict JSON array — no markdown fences, no extra text.
        // Parser strips fences anyway as a fallback: models often ignore this instruction.
        const val SYSTEM_PROMPT =
            "Kamu adalah generator flashcard. Berdasarkan topik yang diberikan user, hasilkan " +
            "8-10 pasangan pertanyaan-jawaban dalam format JSON array murni: " +
            "[{\"question\": \"...\", \"answer\": \"...\"}]. Jangan tambahkan teks lain, " +
            "penjelasan, atau markdown code fence. Hanya JSON valid."
    }
}
