package com.noljanolja.server.core.rest.request

data class UpdateConversationRequest(
    val title: String?,
    val imageUrl: String?,
    val participantIds: Set<String>?,
)