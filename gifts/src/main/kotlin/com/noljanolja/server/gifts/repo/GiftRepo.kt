package com.noljanolja.server.gifts.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GiftRepo : CoroutineCrudRepository<GiftModel, Long> {
    @Query(
        """
        SELECT * FROM gifts WHERE 
        IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) 
        LIMIT :limit  OFFSET :offset
        ORDER BY created_at DESC
    """
    )
    fun findAllBy(
        categoryId: Long?,
        brandId: Long?,
        limit: Int,
        offset: Int,
    ): Flow<GiftModel>

    @Query(
        """
        SELECT * FROM gifts WHERE id = :giftId FOR UPDATE 
    """
    )
    suspend fun findByIdForUpdate(
        giftId: Long,
    ): GiftModel?

    @Query("""
        SELECT gifts.* FROM gifts INNER JOIN gift_codes ON gifts.id = gift_codes.gift_id 
        WHERE gift_codes.user_id = :userId AND
        IF(:categoryId IS NOT NULL, gifts.category_id = :categoryId, TRUE) AND
        IF(:brandId IS NOT NULL, gifts.brand_id = :brandId, TRUE) 
        LIMIT :limit  OFFSET :offset
        ORDER BY created_at DESC
    """)
    suspend fun findGiftsOfUser(
        userId: String,
        categoryId: Long?,
        brandId: Long?,
        limit: Int,
        offset: Int,
    ): Flow<GiftModel>
}