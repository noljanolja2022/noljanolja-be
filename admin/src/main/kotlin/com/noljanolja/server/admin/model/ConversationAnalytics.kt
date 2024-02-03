package com.noljanolja.server.admin.model

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
    val minMessageCount: Long,
    val maxMessageCount: Long
)