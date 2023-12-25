package com.noljanolja.server.consumer.model

import java.time.Instant

data class LoyaltyPoint(
    val id: Long,
    val type: Type,
    val unit: Unit,
    val reasonLocale: String,
    val reason: String,
    val amount: Long,
    val status: Status,
    val createdAt: Instant,
    val log: String?
) {
    enum class Status {
        COMPLETED,
    }

    enum class Type {
        RECEIVED,
        SPENT
    }

    enum class Unit {
        POINT,
        COIN
    }
}