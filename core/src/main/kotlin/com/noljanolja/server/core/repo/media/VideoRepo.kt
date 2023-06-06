package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRepo : CoroutineCrudRepository<VideoModel, String> {
    fun findAllByIdNotIn(
        ids: List<String>,
        pageable: Pageable,
    ): Flow<VideoModel>

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


    @Modifying
    @Query(
        """
            UPDATE `videos`
            SET `like_count`    = :likeCount,
                `comment_count` = :commentCount,
                `view_count`    = :viewCount
            WHERE id = :videoId;
        """
    )
    suspend fun updateCommonStatistics(videoId: String, viewCount: Long, likeCount: Long, commentCount: Long)

    @Modifying
    @Query(
        """
            UPDATE `videos`
            SET `view_count`    = `view_count` + 1
            WHERE id = :videoId;
        """
    )
    suspend fun addLike(videoId: String)
}