package com.noljanolja.server.core.model

import java.time.Instant

data class UserTransferPoint (
    val id: Long,
    val fromUserId: String,
    val toUserId: String,
    val points: Long,
    val type: Type,
    val createdAt: Instant
) {
    enum class Type {
        REQUEST,
        SEND
    }
}