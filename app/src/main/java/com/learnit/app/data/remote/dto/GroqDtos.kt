package com.learnit.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponse(
    val choices: List<GroqChoice>
)

@Serializable
data class GroqChoice(
    val message: GroqMessage,
    @SerialName("finish_reason") val finishReason: String? = null
)

// Internal parse type — not exposed outside data.remote
@Serializable
internal data class FlashcardItem(
    val question: String,
    val answer: String
)
