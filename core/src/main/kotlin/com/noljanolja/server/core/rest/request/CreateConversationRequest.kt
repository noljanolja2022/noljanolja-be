package com.noljanolja.server.core.rest.request

import com.noljanolja.server.core.model.Conversation

data class CreateConversationRequest(
    val title: String,
    val participantIds: MutableSet<String>,
    val type: Conversation.Type,
    val creatorId: String,
    val imageUrl: String,
)