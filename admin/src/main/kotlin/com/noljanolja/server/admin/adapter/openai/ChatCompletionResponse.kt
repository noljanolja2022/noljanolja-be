package com.noljanolja.server.admin.adapter.openai

import kotlinx.serialization.SerialName

data class ChatCompletionResponse(
    val id: String,
    val created: Long,
    val model: String,
    val usage: TokenUsage = TokenUsage(),
    val choices: List<Choice>,
) {
    data class Choice(
        val message: Message,
        @SerialName("finish_reason")
        val finishReason: String = "",
        val index: Long = 0,
    ) {
        data class Message(
            val role: String,
            val content: String,
        )
    }
}

data class TokenUsage(
    @SerialName("prompt_tokens")
    val promptTokens: Long = 0,
    @SerialName("completion_tokens")
    val completionTokens: Long = 0,
    @SerialName("total_tokens")
    val totalTokens: Long = 0,
)
