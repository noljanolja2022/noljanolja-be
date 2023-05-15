package com.noljanolja.server.core.repo.sticker

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StickerPackRepo : CoroutineCrudRepository<StickerPackModel, Long> {
    fun findAllByIsActive(isActive : Boolean = true) : Flow<StickerPackModel>

    @Query(
        """
        UPDATE sticker_packs SET is_active = 0 WHERE id = :stickerId
    """
    )
    suspend fun disableStickerPack(stickerId: Long)

    @Query(
        """
        UPDATE sticker_packs SET is_active = 1 WHERE id = :stickerId
    """
    )
    suspend fun reActivateSticker(stickerId : Long)
}