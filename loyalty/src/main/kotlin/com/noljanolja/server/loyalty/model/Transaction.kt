package com.noljanolja.server.loyalty.model

import java.time.Instant

data class Transaction(
    val id: Long,
    val reason: String,
    val amount: Long,
    val status: Status = Status.COMPLETED,
    val createdAt: Instant,
    val log: String?
) {
    enum class Status {
        COMPLETED,
    }

    enum class Type {
        SPENT,
        RECEIVED
    }
}
