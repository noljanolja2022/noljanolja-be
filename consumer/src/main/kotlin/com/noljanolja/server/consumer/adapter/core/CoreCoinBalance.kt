package com.noljanolja.server.consumer.adapter.core

data class CoreCoinBalance(
    val balance: Double,
)

data class CoreCoinExchangeConfig(
    var point: Int = 0,
    var coin: Int = 0,
    var rewardRecurringAmount: Int = 0,
)