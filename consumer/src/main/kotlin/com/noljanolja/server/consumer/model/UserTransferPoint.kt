package com.noljanolja.server.consumer.model

import com.noljanolja.server.consumer.adapter.core.CoreUserTransferPoint
import java.time.Instant

data class UserTransferPoint(
    val id: Long,
    val fromUserId: String,
    val toUserId: String,
    val points: Long,
    val type: CoreUserTransferPoint.Type,
    val createdAt: Instant
)