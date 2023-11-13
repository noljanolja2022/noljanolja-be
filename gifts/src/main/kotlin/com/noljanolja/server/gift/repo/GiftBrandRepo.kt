package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GiftBrandRepo : CoroutineCrudRepository<GiftBrandModel, String> {
    fun findAllBy(
        pageable: Pageable,
    ): Flow<GiftBrandModel>

    fun findAllByNameContains(name: String, pageable: Pageable): Flow<GiftBrandModel>
    suspend fun countByNameContains(name: String): Long
}