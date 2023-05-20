package com.noljanolja.server.loyalty.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface TransactionRepo : CoroutineCrudRepository<TransactionModel, Long> {
    fun findAllByMemberIdAndCreatedAtIsBeforeOrderByCreatedAtDesc(
        memberId: String,
        timestamp: Instant,
        pageable: Pageable,
    ): Flow<TransactionModel>

    @Query(
        """
            SELECT * FROM transactions WHERE member_id = :memberId 
            AND MONTH(created_at) = :month
            AND YEAR(created_at) = :year
            ORDER BY created_at DESC;
        """
    )
    fun findAllByMemberIdAndMonthYear(
        memberId: String,
        month: Int,
        year: Int,
    ): Flow<TransactionModel>

    fun findAllByMemberIdAndCreatedAtIsAfterOrderByCreatedAtAsc(
        memberId: String,
        timestamp: Instant,
    ): Flow<TransactionModel>
}