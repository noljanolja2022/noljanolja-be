package com.noljanolja.server.consumer.rest.request

data class UpdateMemberOfConversationRequest(
    val participantIds: List<String> = emptyList()
)

data class UpdateAdminOfConversationReq(
    val assigneeId: String
)

data class CoreUpdateMemberOfConversationReq(
    val userId: String,
    val participantIds: List<String> = emptyList()
)

data class CoreUpdateAdminOfConversationReq(
    val adminId: String,
    val assigneeId: String
)

enum class ConversationUpdateType {
    TITLE, AVATAR, ADMIN
}