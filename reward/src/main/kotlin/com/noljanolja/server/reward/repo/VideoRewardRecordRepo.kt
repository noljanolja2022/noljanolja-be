package com.noljanolja.server.reward.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRewardRecordRepo : CoroutineCrudRepository<VideoRewardRecordModel, Long> {
    fun findAllByUserIdAndConfigIdIn(
        userId: String,
        configIds: List<Long>,
    ): Flow<VideoRewardRecordModel>
}