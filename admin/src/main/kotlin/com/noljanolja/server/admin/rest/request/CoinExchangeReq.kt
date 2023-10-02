package com.noljanolja.server.admin.rest.request

data class CoinExchangeReq(
    var coinToPointRate: Double = 0.0,
    var rewardRecurringAmount: Int = 0,
)