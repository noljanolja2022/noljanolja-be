package com.noljanolja.server.consumer.model

import java.time.Instant

data class LoyaltyPoint(
    val id: Long,
    val type: Type,
    val unit: Unit,
    val reason: String,
    val amount: Long,
    val status: Status,
    val createdAt: Instant
) {
    enum class Status {
        COMPLETED,
    }

    enum class Type {
        RECEIVED,
        PAY
    }

    enum class Unit {
        POINT,
        COIN
    }
}