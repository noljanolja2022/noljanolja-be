package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRepo : CoroutineCrudRepository<VideoModel, String> {
    @Query(
        """
            SELECT * FROM videos WHERE 
            IF(:isHighlighted IS NOT NULL, is_highlighted = :isHighlighted, TRUE) AND
            IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """
    )
    fun findAllBy(
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
        offset: Int,
        limit: Int,
    ): Flow<VideoModel>

    @Query(
        """
            SELECT COUNT(*) FROM videos WHERE 
            IF(:isHighlighted IS NOT NULL, is_highlighted = :isHighlighted, TRUE) AND
            IF(:categoryId IS NOT NULL, category_id = :categoryId, TRUE)
        """
    )
    suspend fun countAllBy(
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
    ): Long
}