package com.noljanolja.server.reward.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRewardProgressConfigRepo : CoroutineCrudRepository<VideoRewardProgressConfigModel, Long> {
    suspend fun deleteAllByConfigId(
        configId: Long,
    )

    fun findAllByConfigId(
        configId: Long,
    ): Flow<VideoRewardProgressConfigModel>

    fun findAllByConfigIdIn(
        configIds: List<Long>,
    ): Flow<VideoRewardProgressConfigModel>
}