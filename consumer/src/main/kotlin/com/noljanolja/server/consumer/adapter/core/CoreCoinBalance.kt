package com.noljanolja.server.consumer.adapter.core

data class CoreCoinBalance(
    val balance: Double,
)

data class CoreCoinExchangeConfig(
    var coinToPointRate: Double = 0.0,
    var rewardRecurringAmount: Int = 0,
)