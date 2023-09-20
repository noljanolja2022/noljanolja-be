package com.noljanolja.server.coin_exchange.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface CoinTransactionRepo : CoroutineCrudRepository<CoinTransactionModel, Long> {
    fun findAllByBalanceIdAndCreatedAtIsBeforeOrderByCreatedAtDesc(
        balanceId: Long,
        timestamp: Instant,
        pageable: Pageable,
    ): Flow<CoinTransactionModel>

    @Query(
        """
            SELECT * FROM coin_transactions WHERE balance_id = :balanceId 
            AND MONTH(created_at) = :month
            AND YEAR(created_at) = :year
            ORDER BY created_at DESC;
        """
    )
    fun findAllByBalanceIdAndMonthYear(
        balanceId: Long,
        month: Int,
        year: Int,
    ): Flow<CoinTransactionModel>
}