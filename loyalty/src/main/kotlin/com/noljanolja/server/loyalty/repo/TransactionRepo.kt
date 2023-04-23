package com.noljanolja.server.loyalty.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface TransactionRepo : CoroutineCrudRepository<TransactionModel, Long> {
    fun findAllByMemberIdOrderByCreatedAtDesc(
        memberId: String,
        pageable: Pageable,
    ): Flow<TransactionModel>

    suspend fun countByMemberId(
        memberId: String,
    ): Long

    fun findAllByMemberIdAndCreatedAtIsAfterOrderByCreatedAtAsc(
        memberId: String,
        expireTimestamp: Instant,
    ): Flow<TransactionModel>
}