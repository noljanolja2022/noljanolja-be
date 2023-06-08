package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GiftBrandRepo : CoroutineCrudRepository<GiftBrandModel, Long> {
    fun findAllBy(
        pageable: Pageable,
    ): Flow<GiftBrandModel>
}