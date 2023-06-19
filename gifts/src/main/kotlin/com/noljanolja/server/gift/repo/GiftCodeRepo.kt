package com.noljanolja.server.gift.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GiftCodeRepo : CoroutineCrudRepository<GiftCodeModel, Long> {
    fun findAllByGiftId(
        giftId: Long,
    ): Flow<GiftCodeModel>

    @Query(
        """
            SELECT * FROM gift_codes WHERE gift_id = :giftId AND
            IF(:userId IS NOT NULL, user_id = :userId, true)
        """
    )
    fun findAllByGiftIdAndUserId(
        giftId: Long,
        userId: String? = null,
    ): Flow<GiftCodeModel>

    suspend fun countByUserIdAndGiftId(
        userId: String,
        giftId: Long,
    ): Long

    suspend fun findFirstByGiftIdAndUserIdIsNull(
        giftId: Long
    ): GiftCodeModel?
}