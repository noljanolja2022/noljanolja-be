package com.noljanolja.server.core.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ConversationAnalytics(
    val totalConversations: Long,
    val numOfSingleConversations: Long,
    val numOfGroupConversations: Long,
    val totalMessages: Long,
    val totalStickers: Long,
    val averageMessagesPerConversation: Double,
    val messageRange: MessageRange

)

data class MessageRange(
    @JsonProperty("min_message_count")
    val minMessageCount: Long,
    @JsonProperty("max_message_count")
    val maxMessageCount: Long
)