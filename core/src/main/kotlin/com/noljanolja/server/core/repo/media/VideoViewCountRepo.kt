package com.noljanolja.server.core.repo.media

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoViewCountRepo : CoroutineCrudRepository<VideoViewCountModel, Long> {
    @Query(
        """
            SELECT * FROM video_view_counts WHERE video_id = :videoId ORDER BY created_at DESC LIMIT 1 FOR UPDATE
        """
    )
    suspend fun findNewestVideoById(
        videoId: String,
    ): VideoViewCountModel?

    @Query(
        """
            SELECT SUM(view_count) FROM video_view_counts WHERE video_id = :videoId
        """
    )
    suspend fun getTotalViewCount(
        videoId: String,
    ): Long

    @Query(
        """
            SELECT videos.* 
            FROM videos 
            INNER JOIN 
                (
                    SELECT video_id, SUM(view_count) as total_view_count 
                    FROM video_view_counts 
                    WHERE 
                        created_at >= (CURRENT_DATE() - INTERVAL :days DAY) 
                    GROUP BY video_id LIMIT :limit
                ) as v
            ON videos.id = v.video_id 
            WHERE 
                IF(:userId IS NOT NULL AND :isExcludeIgnoredVideos IS TRUE, videos.id NOT IN (SELECT video_id FROM video_users WHERE is_ignored = TRUE AND user_id = :userId), TRUE) AND
                IF(:includeDeleted IS NULL OR :includeDeleted IS FALSE, videos.deleted_at IS NULL, TRUE) AND 
                IF (:includeDeactivated IS NULL OR :includeDeactivated IS FALSE, is_deactivated IS FALSE, TRUE) AND
                IF (:includeUnavailableVideos IS NULL OR :includeUnavailableVideos IS FALSE, (available_from IS NULL OR (available_from IS NOT NULL AND available_from <= NOW())) AND (available_to IS NULL OR (available_to IS NOT NULL AND NOW() <= available_to)), TRUE)
            ORDER BY v.total_view_count DESC
        """
    )
    suspend fun findTopTrendingVideos(
        days: Int,
        limit: Int,
        userId: String? = null,
        isExcludeIgnoredVideos: Boolean? = null,
        includeDeleted: Boolean? = null,
        includeDeactivated: Boolean? = null,
        includeUnavailableVideos: Boolean? = null,
    ): Flow<VideoModel>
}