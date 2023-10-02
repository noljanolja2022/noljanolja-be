package com.noljanolja.server.coin_exchange.model.request

data class ExchangePointRequest(
    val points: Long,
)

data class CoinExchangeReq(
    var coinToPointRate: Double = 0.0,
    var rewardRecurringAmount: Int = 0,
)