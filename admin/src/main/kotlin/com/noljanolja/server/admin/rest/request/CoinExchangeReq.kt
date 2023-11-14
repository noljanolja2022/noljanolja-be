package com.noljanolja.server.admin.rest.request

data class CoinExchangeReq(
    val coin: Int = 0,
    val point: Int = 0,
    val rewardRecurringAmount: Int = 0,
)