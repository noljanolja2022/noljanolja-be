package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.UserTransferPoint
import java.time.Instant

data class CoreUserTransferPoint(
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

fun CoreUserTransferPoint.toUserTransferPoint() = UserTransferPoint(
    id = id,
    fromUserId = fromUserId,
    toUserId = toUserId,
    points = points,
    type = type,
    createdAt = createdAt
)