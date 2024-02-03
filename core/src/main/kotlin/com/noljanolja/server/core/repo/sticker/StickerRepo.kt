package com.noljanolja.server.core.repo.sticker

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StickerRepo : CoroutineCrudRepository<StickerModel, Long> {
    fun findAllByPackId(packId: Long): Flow<StickerModel>
    @Query(
        """
            SELECT COUNT(*) FROM stickers
        """
    )
    suspend fun countAll(): Long
}