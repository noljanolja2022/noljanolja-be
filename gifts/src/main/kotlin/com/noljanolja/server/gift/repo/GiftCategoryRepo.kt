package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface GiftCategoryRepo : CoroutineCrudRepository<GiftCategoryModel, Long> {
    fun findAllBy(
        pageable: Pageable,
    ): Flow<GiftCategoryModel>

    @Query(
        """
            SELECT * FROM gift_categories 
            WHERE IF(:query IS NOT NULL, name LIKE CONCAT('%',:query,'%'), TRUE)
            LIMIT :limit  OFFSET :offset
        """
    )
    fun findByNameContainsAndLocale(
        query: String?,
        limit: Int,
        offset: Int
    ): Flow<GiftCategoryModel>

    @Query(
        """
            SELECT COUNT(*) FROM gift_categories 
            WHERE IF(:query IS NOT NULL, name LIKE CONCAT('%',:query,'%'), TRUE)
        """
    )
    suspend fun countByNameContainsAndLocale(
        query: String?
    ): Long
}