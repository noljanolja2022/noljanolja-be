package com.noljanolja.server.consumer.rest.request

import com.noljanolja.server.consumer.model.Conversation

data class CreateConversationRequest(
    val title: String,
    val participantIds: Set<String>,
    val type: Conversation.Type,
    val imageUrl: String,
)
