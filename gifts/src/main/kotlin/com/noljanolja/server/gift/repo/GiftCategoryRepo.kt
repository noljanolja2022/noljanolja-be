package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface GiftCategoryRepo : CoroutineCrudRepository<GiftCategoryModel, Long> {
    fun findAllBy(
        pageable: Pageable,
    ): Flow<GiftCategoryModel>

    fun findAllByNameContains(name: String, pageable: Pageable): Flow<GiftCategoryModel>
    suspend fun countByNameContains(name: String): Long
}