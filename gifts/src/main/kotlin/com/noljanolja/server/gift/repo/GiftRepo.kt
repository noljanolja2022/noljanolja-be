package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GiftRepo : CoroutineCrudRepository<GiftModel, Long> {

    fun findAllByNameIn(
        name: List<String>
    ):  Flow<GiftModel>

    @Query(
        """
        SELECT * FROM gifts WHERE 
        IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) 
        ORDER BY created_at DESC
        LIMIT :limit  OFFSET :offset
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
        SELECT COUNT(*) FROM gifts WHERE 
        IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE) AND
        IF(:brandId IS NOT NULL, brand_id = :brandId, TRUE) 
    """
    )
    suspend fun countAllBy(
        categoryId: Long?,
        brandId: Long?,
    ): Long

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
        ORDER BY created_at DESC
        LIMIT :limit  OFFSET :offset
    """)
    suspend fun findGiftsOfUser(
        userId: String,
        categoryId: Long?,
        brandId: Long?,
        limit: Int,
        offset: Int,
    ): Flow<GiftModel>

    @Query("""
        SELECT COUNT(*) FROM gifts INNER JOIN gift_codes ON gifts.id = gift_codes.gift_id 
        WHERE gift_codes.user_id = :userId AND
        IF(:categoryId IS NOT NULL, gifts.category_id = :categoryId, TRUE) AND
        IF(:brandId IS NOT NULL, gifts.brand_id = :brandId, TRUE) 
    """)
    suspend fun countGiftsOfUser(
        userId: String,
        categoryId: Long?,
        brandId: Long?,
    ): Long
}