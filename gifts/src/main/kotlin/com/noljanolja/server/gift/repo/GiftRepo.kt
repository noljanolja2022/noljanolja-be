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
        IF(:isActive IS NOT NULL, is_active = :isActive, TRUE) AND
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) AND
        IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
        IF(:isFeatured IS NOT NULL, is_featured = :isFeatured, TRUE) AND
        IF(:isTodayOffer IS NOT NULL, is_today_offer = :isTodayOffer, TRUE) AND
        IF(:query IS NOT NULL, 
            name LIKE CONCAT('%',:query,'%') OR 
            category_id IN (SELECT id FROM gift_categories WHERE name LIKE CONCAT('%',:query,'%')) OR
            brand_id IN (SELECT id FROM gift_brands WHERE name LIKE CONCAT('%',:query,'%')), 
            TRUE
        ) AND
        IF(:isRecommended IS TRUE, 
            id IN (SELECT gift_id FROM gift_transactions GROUP BY gift_id ORDER BY COUNT(gift_id) DESC),            
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
        isTodayOffer: Boolean?,
        limit: Int,
        offset: Int,
        query: String?,
        isRecommended: Boolean?
    ): Flow<GiftModel>

    @Query(
        """
        SELECT COUNT(*) FROM gifts WHERE 
        IF(:isActive IS NOT NULL, is_active = :isActive, TRUE) AND
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) AND
        IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
        IF(:isFeatured IS NOT NULL, is_featured = :isFeatured, TRUE) AND
        IF(:isTodayOffer IS NOT NULL, is_today_offer = :isTodayOffer, TRUE) AND
        IF(:query IS NOT NULL, 
            name LIKE CONCAT('%',:query,'%') OR 
            category_id IN (SELECT id FROM gift_categories WHERE name LIKE CONCAT('%',:query,'%')) OR
            brand_id IN (SELECT id FROM gift_brands WHERE name LIKE CONCAT('%',:query,'%')), 
            TRUE
        ) AND
        IF(:isRecommended IS TRUE, 
            id IN (SELECT gift_id FROM gift_transactions GROUP BY gift_id ORDER BY COUNT(gift_id) DESC),            
            TRUE
        )
    """
    )
    suspend fun countAllBy(
        isActive: Boolean?,
        brandId: String?,
        categoryId: Long?,
        isFeatured: Boolean?,
        query: String?,
        isTodayOffer: Boolean?,
        isRecommended: Boolean?
    ): Long
}