package com.noljanolja.server.core.rest.request

data class UserBlockUserRequest(
    val blockedUserId: String,
    val isBlocked: Boolean,
)
