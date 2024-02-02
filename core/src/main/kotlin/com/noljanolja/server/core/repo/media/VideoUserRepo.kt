package com.noljanolja.server.core.repo.media

import com.noljanolja.server.core.model.LikeStatistics
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoUserRepo : CoroutineCrudRepository<VideoUserModel, Long> {
    suspend fun findByVideoIdAndUserId(
        videoId: String,
        userId: String,
    ): VideoUserModel?

    suspend fun countAllByIsLikedIsTrueAndVideoId(
        videoId: String
    ): Long

    @Query(
        """
            SELECT video_id, COUNT(id) AS like_count
            FROM video_users
            WHERE is_liked = 1
            GROUP BY video_id;
        """
    )
    fun findLikeStatistics(): Flow<LikeStatistics>
}