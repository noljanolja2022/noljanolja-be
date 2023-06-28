package com.noljanolja.server.reward.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoLikeRewardRecordRepo : CoroutineCrudRepository<VideoLikeRewardRecordModel, Long> {
    suspend fun findByUserIdAndConfigId(
        userId: String,
        configId: Long,
    ): VideoLikeRewardRecordModel?

    suspend fun existsByUserIdAndConfigId(
        userId: String,
        configId: Long,
    ): Boolean
}