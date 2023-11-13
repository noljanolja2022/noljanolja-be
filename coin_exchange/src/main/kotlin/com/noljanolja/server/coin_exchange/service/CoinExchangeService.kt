package com.noljanolja.server.coin_exchange.service

import com.noljanolja.server.coin_exchange.exception.Error
import com.noljanolja.server.coin_exchange.model.CoinTransaction
import com.noljanolja.server.coin_exchange.model.request.CoinExchangeReq
import com.noljanolja.server.coin_exchange.repo.*
import com.noljanolja.server.common.utils.REASON_EXCHANGE_POINT
import com.noljanolja.server.loyalty.service.LoyaltyService
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant as JavaInstant

@Component
@Transactional
class CoinExchangeService(
    private val userBalanceRepo: UserBalanceRepo,
    private val coinTransactionRepo: CoinTransactionRepo,
    private val exchangeRateRepo: ExchangeRateRepo,
    private val loyaltyService: LoyaltyService,
) {
    suspend fun getCoinExchangeConfig(): ExchangeRateModel {
        return exchangeRateRepo.findFirstBy() ?: ExchangeRateModel()
    }

    suspend fun updateCoinToPointConfig(payload: CoinExchangeReq): ExchangeRateModel {
        val cachedConfig = (exchangeRateRepo.findFirstBy() ?: ExchangeRateModel()).apply {
            coinToPointRate = payload.coinToPointRate
            rewardRecurringAmount = payload.rewardRecurringAmount
        }
        return exchangeRateRepo.save(cachedConfig)
    }

    suspend fun exchangePointToCoin(
        points: Long,
        userId: String,
    ): CoinTransaction {
        loyaltyService.addTransaction(
            memberId = userId,
            points = -points,
            reason = REASON_EXCHANGE_POINT,
        )
        val coinToPointRate = getCoinExchangeConfig().coinToPointRate
        if (coinToPointRate == 0.0) throw Error.InsufficientCoinBalance
        val receivedCoinAmounts = points * coinToPointRate
        return addTransaction(
            userId = userId,
            amount = receivedCoinAmounts,
            reason = REASON_EXCHANGE_POINT
        )
    }

    suspend fun getUserBalance(userId: String) = (userBalanceRepo.findByUserId(userId) ?: kotlin.run {
        userBalanceRepo.save(UserBalanceModel(userId = userId))
    }).toUserBalance()

    suspend fun addTransaction(
        userId: String,
        amount: Double,
        reason: String,
    ): CoinTransaction {
        val userBalance = userBalanceRepo.findByUserId(userId) ?: UserBalanceModel(userId = userId)
        if (userBalance.balance + amount < 0) throw Error.InsufficientCoinBalance
        userBalance.balance += amount
        val savedBalance = userBalanceRepo.save(userBalance)
        return coinTransactionRepo.save(
            CoinTransactionModel(
                balanceId = savedBalance.id,
                amount = amount,
                reason = reason,
            )
        ).toCoinTransaction()
    }

    suspend fun getTransactions(
        userId: String,
        lastOffsetDate: JavaInstant? = null,
        type: CoinTransaction.Type? = null,
        month: Int? = null,
        year: Int? = null,
        pageSize: Int = 20,
    ): List<CoinTransaction> {
        val balanceId = userBalanceRepo.findByUserId(userId)?.id ?: return emptyList()
        val transactions = if (listOfNotNull(lastOffsetDate, month, year).isEmpty() || lastOffsetDate != null) {
            coinTransactionRepo.findAllByBalanceIdAndCreatedAtIsBeforeOrderByCreatedAtDesc(
                balanceId = balanceId,
                timestamp = lastOffsetDate ?: JavaInstant.now(),
                pageable = Pageable.ofSize(pageSize)
            ).toList()
        } else if (month != null && year != null) {
            coinTransactionRepo.findAllByBalanceIdAndMonthYear(
                balanceId = balanceId,
                month = month,
                year = year,
            ).toList()
        } else {
            emptyList()
        }
        return transactions.filter {
            type == null ||
                    (type == CoinTransaction.Type.RECEIVED && it.amount > 0) ||
                    (type == CoinTransaction.Type.SPENT && it.amount < 0)
        }.map { it.toCoinTransaction() }
    }
}