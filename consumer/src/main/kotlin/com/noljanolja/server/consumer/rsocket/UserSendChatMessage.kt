package com.noljanolja.server.consumer.rsocket

import com.noljanolja.server.consumer.model.Conversation

data class UserSendChatMessage(
    val userId: String,
    val conversationId: Long,
    val roomType: Conversation.Type,
)
