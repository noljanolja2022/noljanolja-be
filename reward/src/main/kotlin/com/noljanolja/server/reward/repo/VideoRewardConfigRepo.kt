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

    fun findAllBy(pageable: Pageable): Flow<VideoRewardConfigModel>
}