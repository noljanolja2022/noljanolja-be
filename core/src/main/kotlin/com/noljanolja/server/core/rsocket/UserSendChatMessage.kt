package com.noljanolja.server.core.rsocket

import com.noljanolja.server.reward.repo.RoomType

data class UserSendChatMessage(
    val userId: String,
    val conversationId: Long,
    val roomType: RoomType,
    val creatorId: String,
)
