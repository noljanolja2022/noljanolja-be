package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PromotedVideoRepo : CoroutineCrudRepository<PromotedVideoModel, Long> {

    @Query(
        """
        SELECT promoted_videos.* 
        FROM promoted_videos 
        INNER JOIN videos 
        ON promoted_videos.video_id = videos.id 
        WHERE IF(:includeDeleted IS NULL OR :includeDeleted IS FALSE, videos.deleted_at IS NULL, TRUE)
    """
    )
    fun findAllBy(includeDeleted: Boolean? = null, pageable: Pageable): Flow<PromotedVideoModel>

    @Query(
    """
        SELECT videos.* 
        FROM promoted_videos 
        INNER JOIN videos 
        ON promoted_videos.video_id = videos.id 
        WHERE
            IF(:userId IS NOT NULL AND :isExcludeIgnoredVideos IS TRUE, videos.id NOT IN (SELECT video_id FROM video_users WHERE is_ignored = TRUE AND user_id = :userId), TRUE) AND
            IF(:includeDeleted IS NULL OR :includeDeleted IS FALSE, videos.deleted_at IS NULL, TRUE)
        LIMIT :limit OFFSET :offset
    """
    )
    fun findAllBy(
        offset: Int,
        limit: Int,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
    ): Flow<VideoModel>

    @Query(
        """
        SELECT video_id from promoted_videos WHERE CURRENT_DATE() NOT BETWEEN start_date AND end_date
    """
    )
    fun findAllOutdatedVideos(): Flow<Long>

    @Query(
        """
            SELECT COUNT(*) 
            FROM promoted_videos 
            INNER JOIN videos 
            ON promoted_videos.video_id = videos.id 
            WHERE
                IF(:userId IS NOT NULL AND :isExcludeIgnoredVideos IS TRUE, videos.id NOT IN (SELECT video_id FROM video_users WHERE is_ignored = TRUE AND user_id = :userId), TRUE) AND
                IF(:includeDeleted IS NULL OR :includeDeleted IS FALSE, videos.deleted_at IS NULL, TRUE)
        """
    )
    suspend fun countAllBy(
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
    ): Long
}