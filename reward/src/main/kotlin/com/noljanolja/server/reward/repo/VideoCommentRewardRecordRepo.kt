package com.noljanolja.server.reward.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoCommentRewardRecordRepo : CoroutineCrudRepository<VideoCommentRewardRecordModel, Long> {
    suspend fun countAllByUserIdAndConfigId(
        userId: String,
        configId: Long
    ): Long
}