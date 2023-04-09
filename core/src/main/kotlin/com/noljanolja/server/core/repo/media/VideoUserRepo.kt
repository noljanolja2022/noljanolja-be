package com.noljanolja.server.core.repo.media

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoUserRepo : CoroutineCrudRepository<VideoUserModel, Long> {
    suspend fun findByVideoIdAndUserId(
        videoId: String,
        userId: String,
    ): VideoUserModel?

    @Query(
        """
            SELECT COUNT(*) FROM video_users WHERE video_id = :videoId AND is_liked = true
        """
    )
    suspend fun getTotalLike(
        videoId: String
    ): Long
}