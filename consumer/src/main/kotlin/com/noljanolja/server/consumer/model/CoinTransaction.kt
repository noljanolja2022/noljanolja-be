package com.noljanolja.server.consumer.model

import java.time.Instant

data class CoinTransaction(
    val id: Long,
    val balanceId: Long,
    val reason: String,
    val amount: Double,
    val status: Status = Status.COMPLETED,
    val createdAt: Instant,
) {
    enum class Status {
        COMPLETED,
    }

    enum class Type {
        SPENT,
        RECEIVED
    }
}
