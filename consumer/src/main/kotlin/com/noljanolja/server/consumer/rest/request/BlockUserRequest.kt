package com.noljanolja.server.consumer.rest.request

data class BlockUserRequest(
    val isBlocked: Boolean,
    val blockedUserId: String,
)
