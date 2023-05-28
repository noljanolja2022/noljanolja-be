package com.noljanolja.server.reward.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRewardConfigRepo : CoroutineCrudRepository<VideoRewardConfigModel, Long> {
    fun findAllByVideoIdInAndActiveIsTrue(videoIds: Set<String>): Flow<VideoRewardConfigModel>

    suspend fun findByVideoId(videoId: String): VideoRewardConfigModel?

    @Query(
        """
            SELECT * FROM video_reward_configs WHERE video_id = :videoId FOR UPDATE
        """
    )
    suspend fun findByVideoIdForUpdate(
        videoId: String
    ): VideoRewardConfigModel?

    fun findAllBy(pageable: Pageable): Flow<VideoRewardConfigModel>
}