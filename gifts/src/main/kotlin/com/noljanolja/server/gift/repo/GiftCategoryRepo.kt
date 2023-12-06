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
            SELECT * FROM gift_categories WHERE
            IF(:query IS NOT NULL, name LIKE CONCAT('%',:query,'%'), TRUE) AND
            IF(:locale IS NOT NULL, locale = :locale, FALSE)
            LIMIT :limit  OFFSET :offset
        """
    )
    fun findByNameContainsAndLocale(
        query: String?,
        locale: String?,
        limit: Int,
        offset: Int
    ): Flow<GiftCategoryModel>

    @Query(
        """
            SELECT COUNT(*) FROM gift_categories WHERE
            IF(:query IS NOT NULL, name LIKE CONCAT('%',:query,'%'), TRUE) AND
            IF(:locale IS NOT NULL, locale = :locale, FALSE)
        """
    )
    suspend fun countByNameContainsAndLocale(
        query: String?,
        locale: String?,
    ): Long
}