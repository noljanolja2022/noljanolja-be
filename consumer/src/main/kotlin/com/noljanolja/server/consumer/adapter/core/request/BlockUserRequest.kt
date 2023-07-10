package com.noljanolja.server.consumer.adapter.core.request

data class BlockUserRequest(
    val blockedUserId: String,
    val isBlocked: Boolean,
)
