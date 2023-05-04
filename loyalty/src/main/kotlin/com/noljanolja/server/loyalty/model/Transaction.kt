package com.noljanolja.server.loyalty.model

import java.time.Instant

data class Transaction(
    val id: Long,
    val reason: String,
    val amount: Long,
    val createdAt: Instant,
)