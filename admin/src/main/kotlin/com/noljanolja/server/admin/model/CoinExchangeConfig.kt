package com.noljanolja.server.admin.model

import java.time.Instant

data class CoinExchangeConfig(
    var coinToPointRate: Double = 0.0,
    var rewardRecurringAmount: Int = 0,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) {
}