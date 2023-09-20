package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toCoinTransaction
import com.noljanolja.server.consumer.config.language.Translator
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CoinExchangeService(
    private val coreApi: CoreApi,
    private val translator: Translator,
) {
    suspend fun getUserBalance(
        userId: String,
    ) = coreApi.getUserCoinBalance(userId)

    suspend fun getCoinToPointExchangeRate() = coreApi.getCoinToPointExchangeRate()

    suspend fun exchangePointToCoin(
        points: Long,
        userId: String,
    ) = coreApi.exchangePointToCoin(
        points = points,
        userId = userId,
    ).toCoinTransaction(translator)

    suspend fun getUserCoinTransactions(
        userId: String,
        lastOffsetDate: Instant? = null,
        type: String? = null,
        month: Int? = null,
        year: Int? = null,
    ) = coreApi.getUserCoinTransactions(
        userId = userId,
        lastOffsetDate = lastOffsetDate,
        type = type,
        month = month,
        year = year,
    ).map { it.toCoinTransaction(translator) }
}