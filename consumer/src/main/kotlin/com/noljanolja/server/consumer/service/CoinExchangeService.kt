package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toCoinTransaction
import com.noljanolja.server.consumer.adapter.gift.GiftApi
import com.noljanolja.server.consumer.config.language.Translator
import com.noljanolja.server.consumer.model.RewardInfo
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CoinExchangeService(
    private val coreApi: CoreApi,
    private val giftApi: GiftApi,
    private val translator: Translator,
) {
    suspend fun getUserBalance(
        userId: String,
    ): RewardInfo {
        val coinBalance = coreApi.getUserCoinBalance(userId)
        val giftCount = giftApi.getUserGiftCount(userId)
        return RewardInfo(
            balance = coinBalance.balance,
            giftCount = giftCount
        )
    }

    suspend fun getPointToCoinExchangeRate() = coreApi.getPointToCoinExchangeRate()

    suspend fun exchangePointToCoin(
        userId: String,
    ) = coreApi.exchangePointToCoin(
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