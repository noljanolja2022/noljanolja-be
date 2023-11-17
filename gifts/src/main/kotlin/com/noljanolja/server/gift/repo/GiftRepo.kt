package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GiftRepo : CoroutineCrudRepository<GiftModel, String> {

    @Query(
        """
        SELECT * FROM gifts WHERE 
        is_active = TRUE AND
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) 
        ORDER BY created_at DESC
        LIMIT :limit  OFFSET :offset
    """
    )
    fun findAllByActive(
        brandId: Long?,
        limit: Int,
        offset: Int,
    ): Flow<GiftModel>

    @Query(
        """
        SELECT * FROM gifts WHERE 
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) 
        ORDER BY created_at DESC
        LIMIT :limit  OFFSET :offset
    """
    )
    fun findAllBy(
        brandId: Long?,
        limit: Int,
        offset: Int,
    ): Flow<GiftModel>

    @Query(
        """
        SELECT COUNT(*) FROM gifts WHERE 
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) 
    """
    )
    suspend fun countAllBy(
        brandId: Long?,
    ): Long

    suspend fun countAllByIsActive(
        isActive: Boolean
    ): Long
}