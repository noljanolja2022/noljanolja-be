package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GiftTransactionRepo : CoroutineCrudRepository<GiftTransactionModel, String> {
    suspend fun findByUserId(userId: String, pageable: Pageable): Flow<GiftTransactionModel>

    suspend fun countAllByUserId(userId: String): Long
}