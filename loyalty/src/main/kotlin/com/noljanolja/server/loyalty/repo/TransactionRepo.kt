package com.noljanolja.server.loyalty.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface TransactionRepo : CoroutineCrudRepository<TransactionModel, Long> {
    @Query(
        """
            SELECT * 
            FROM (
                SELECT CT.id, UB.user_id AS member_id, CT.amount, CT.reason, CT.created_at, CT.log
                FROM coin_transactions CT
                INNER JOIN user_balances UB ON CT.balance_id = UB.id
                WHERE CT.reason = 'REASON_PURCHASE_GIFT'
                UNION 
                SELECT *
                FROM transactions T
            ) AS TMP
            WHERE TMP.member_id = :memberId AND TMP.created_at < :timestamp
            ORDER BY TMP.created_at DESC
            LIMIT :limit
        """
    )
    fun findAllByMemberIdAndCreatedAtIsBeforeOrderByCreatedAtDesc(
        memberId: String,
        timestamp: Instant?,
        limit: Int
    ): Flow<TransactionModel>

    @Query(
        """
            SELECT * 
            FROM (
                SELECT CT.id, UB.user_id AS member_id, CT.amount, CT.reason, CT.created_at, CT.log
                FROM coin_transactions CT
                INNER JOIN user_balances UB ON CT.balance_id = UB.id
                WHERE CT.reason = 'REASON_PURCHASE_GIFT'
                UNION 
                SELECT *
                FROM transactions T
            ) AS TMP
            WHERE TMP.member_id = :memberId AND MONTH(TMP.created_at) = :month AND YEAR(TMP.created_at) = :year
            ORDER BY TMP.created_at DESC
        """
    )
    fun findAllByMemberIdAndMonthYear(
        memberId: String,
        month: Int,
        year: Int
    ): Flow<TransactionModel>

    fun findAllByMemberIdAndCreatedAtIsAfterOrderByCreatedAtAsc(
        memberId: String,
        timestamp: Instant,
    ): Flow<TransactionModel>

    @Query(
        """
            SELECT * 
            FROM transactions
            WHERE id = :transactionId AND member_id = :memberId
        """
    )
    suspend fun findByIdAndMemberId(
        transactionId: Long,
        memberId: String
    ): TransactionModel?

    @Query(
        """
            SELECT CT.id, UB.user_id AS member_id, CT.amount, CT.reason, CT.created_at, CT.log
            FROM coin_transactions CT
            INNER JOIN user_balances UB ON CT.balance_id = UB.id
            WHERE CT.id = :coinTransactionId AND UB.user_id = :memberId
        """
    )
    suspend fun findByCoinTransactionIdAndMemberId(
        coinTransactionId: Long,
        memberId: String
    ): TransactionModel?
}