package com.noljanolja.server.admin.adapter.openai

data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatMessage>,
    val temperature: Double? = null,
) {
    data class ChatMessage(
        val role: String,
        val content: String,
    )
}
