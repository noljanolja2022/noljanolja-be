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
            SELECT videos.* FROM videos INNER JOIN 
            (SELECT video_id, SUM(view_count) as total_view_count FROM video_view_counts WHERE created_at >= (CURRENT_DATE() - INTERVAL :days DAY) GROUP BY video_id LIMIT :limit) as v
            ON videos.id = v.video_id ORDER BY v.total_view_count DESC
        """
    )
    suspend fun findTopTrendingVideos(
        days: Int,
        limit: Int,
    ): Flow<VideoModel>
}