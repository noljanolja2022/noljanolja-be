package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GiftTransactionRepo : CoroutineCrudRepository<GiftTransactionModel, String> {
    suspend fun findByUserId(userId: String): Flow<GiftTransactionModel>

    suspend fun countAllByUserId(userId: String): Long
}