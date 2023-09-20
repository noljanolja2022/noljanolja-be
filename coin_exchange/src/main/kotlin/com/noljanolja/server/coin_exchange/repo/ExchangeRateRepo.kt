package com.noljanolja.server.coin_exchange.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ExchangeRateRepo : CoroutineCrudRepository<ExchangeRateModel, Long> {
    suspend fun findFirstBy(): ExchangeRateModel?
}