package com.noljanolja.server.core.rest.request

data class UpdateConversationRequest(
    val title: String?,
    val imageUrl: String?,
    val participantIds: Set<String>?,
)

data class UpdateConversationParticipantsRequest(
    val userId: String,
    val participantIds: List<String> = emptyList()
)

data class UpdateConversationAdminRequest(
    val adminId: String,
    val assigneeId: String,
)