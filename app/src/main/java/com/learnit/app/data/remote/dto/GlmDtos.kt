package com.learnit.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlmRequest(
    val model: String,
    val messages: List<GlmMessage>
)

@Serializable
data class GlmMessage(
    val role: String,
    val content: String
)

@Serializable
data class GlmResponse(
    val choices: List<GlmChoice>
)

@Serializable
data class GlmChoice(
    val message: GlmMessage,
    @SerialName("finish_reason") val finishReason: String? = null
)

// Internal parse type — not exposed outside data.remote
@Serializable
internal data class FlashcardItem(
    val question: String,
    val answer: String
)
