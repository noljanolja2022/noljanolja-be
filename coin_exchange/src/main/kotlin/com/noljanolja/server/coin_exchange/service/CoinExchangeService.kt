package com.noljanolja.server.coin_exchange.service

import com.noljanolja.server.coin_exchange.model.CoinTransaction
import com.noljanolja.server.coin_exchange.model.request.CoinExchangeReq
import com.noljanolja.server.coin_exchange.repo.*
import com.noljanolja.server.common.exception.CustomBadRequestException
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

    suspend fun updatePointToCoinConfig(payload: CoinExchangeReq): ExchangeRateModel {
        if (payload.point < 1 || payload.coin < 1) throw CustomBadRequestException("Point or coin conversion rate can't be lower than 1")
        val cachedConfig = (exchangeRateRepo.findFirstBy() ?: ExchangeRateModel()).apply {
            point = payload.point
            coin = payload.coin
            rewardRecurringAmount = payload.rewardRecurringAmount
        }
        return exchangeRateRepo.save(cachedConfig)
    }

    suspend fun exchangePointToCoin(
        userId: String,
    ): CoinTransaction {
        val config = getCoinExchangeConfig()
        if (config.point < 1 || config.coin < 1) throw CustomBadRequestException("The current exchange rate is invalid")
        val currentMember = loyaltyService.getMember(userId)
        if (currentMember.point < config.point) {
            throw CustomBadRequestException("Insufficient fund")
        }
        loyaltyService.addTransaction(
            memberId = userId,
            points = -config.point.toLong(),
            reason = REASON_EXCHANGE_POINT,
        )
        return addTransaction(
            userId = userId,
            amount = config.coin.toLong(),
            reason = REASON_EXCHANGE_POINT
        )
    }

    suspend fun getUserBalance(userId: String) = (userBalanceRepo.findByUserId(userId) ?: kotlin.run {
        userBalanceRepo.save(UserBalanceModel(userId = userId))
    }).toUserBalance()

    suspend fun addTransaction(
        userId: String,
        amount: Long,
        reason: String,
        log: String? = null
    ): CoinTransaction {
        val userBalance = userBalanceRepo.findByUserId(userId) ?: UserBalanceModel(userId = userId)
        if (userBalance.balance + amount < 0) throw CustomBadRequestException("Insufficient fund")
        userBalance.balance += amount
        val savedBalance = userBalanceRepo.save(userBalance)
        return coinTransactionRepo.save(
            CoinTransactionModel(
                balanceId = savedBalance.id,
                amount = amount,
                reason = reason,
                log = log
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