package com.noljanolja.server.coin_exchange.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserBalanceRepo : CoroutineCrudRepository<UserBalanceModel, Long> {
    suspend fun findByUserId(userId: String): UserBalanceModel?

}