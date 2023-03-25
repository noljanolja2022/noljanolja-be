package com.noljanolja.server.consumer.adapter.core.request

data class UpdateConversationRequest(
    val title: String? = null,
    val imageUrl: String? = null,
    val participantIds: Set<String>? = null,
)