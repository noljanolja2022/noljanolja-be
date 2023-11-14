package com.noljanolja.server.coin_exchange.model

import java.time.Instant

data class UserBalance(
    val id: Long,
    val balance: Long,
    val createdAt: Instant,
)