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
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) AND
        IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
        IF(:isFeatured IS NOT NULL, is_featured = :isFeatured, TRUE) AND
        IF(:query IS NOT NULL, 
            name LIKE CONCAT('%',:query,'%') OR category_id IN (SELECT id FROM gift_categories WHERE name LIKE CONCAT('%',:query,'%')), 
            TRUE
        )
        ORDER BY created_at DESC
        LIMIT :limit  OFFSET :offset
    """
    )
    fun findAllBy(
        isActive: Boolean?,
        brandId: String?,
        categoryId: Long?,
        isFeatured: Boolean?,
        limit: Int,
        offset: Int,
        query: String?
    ): Flow<GiftModel>

    @Query(
        """
        SELECT COUNT(*) FROM gifts WHERE 
        IF(:isActive IS NOT NULL, is_active = :isActive, TRUE) AND
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) AND
        IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
        IF(:isFeatured IS NOT NULL, is_featured = :isFeatured, TRUE) AND
        IF(:query IS NOT NULL, 
            name LIKE CONCAT('%',:query,'%') OR category_id IN (SELECT id FROM gift_categories WHERE name LIKE CONCAT('%',:query,'%')), 
            TRUE
        )
    """
    )
    suspend fun countAllBy(
        isActive: Boolean?,
        brandId: String?,
        categoryId: Long?,
        isFeatured: Boolean?,
        query: String?
    ): Long
}