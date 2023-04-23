package com.noljanolja.server.loyalty.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TierConfigRepo : CoroutineCrudRepository<TierConfigModel, Long> {

     fun findAllByOrderByMinPointAsc() : Flow<TierConfigModel>
}