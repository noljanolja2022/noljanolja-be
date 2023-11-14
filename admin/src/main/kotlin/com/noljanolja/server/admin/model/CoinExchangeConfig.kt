package com.noljanolja.server.admin.model

import java.time.Instant

data class CoinExchangeConfig(
    val coin: Int = 0,
    val point: Int = 0,
    val rewardRecurringAmount: Int = 0,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)