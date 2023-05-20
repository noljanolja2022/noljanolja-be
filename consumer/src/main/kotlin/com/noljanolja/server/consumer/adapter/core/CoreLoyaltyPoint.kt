package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.LoyaltyPoint
import java.time.Instant

data class CoreLoyaltyPoint(
    val id: Long,
    val reason: String,
    val amount: Long,
    val status: Status,
    val createdAt: Instant,
) {
    enum class Status {
        COMPLETED
    }
}

fun CoreLoyaltyPoint.toConsumerLoyaltyPoint() = LoyaltyPoint(
    id = id,
    reason = reason,
    amount = amount,
    createdAt = createdAt,
    status = LoyaltyPoint.Status.valueOf(status.name),
)