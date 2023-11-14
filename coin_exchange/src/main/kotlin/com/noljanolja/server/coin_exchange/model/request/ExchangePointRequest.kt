package com.noljanolja.server.coin_exchange.model.request

data class CoinExchangeReq(
    var coin: Int = 0,
    var point: Int = 0,
    var rewardRecurringAmount: Int = 0,
)