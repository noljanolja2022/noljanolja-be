package com.noljanolja.server.consumer.adapter.core.request

import com.noljanolja.server.consumer.adapter.core.CoreConversation

data class CreateConversationRequest(
    val title: String,
    val participantIds: Set<String>,
    val type: CoreConversation.Type,
    val creatorId: String,
    val imageUrl: String = "",
)
