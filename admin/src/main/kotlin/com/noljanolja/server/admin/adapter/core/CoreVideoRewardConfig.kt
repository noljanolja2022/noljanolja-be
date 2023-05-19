package com.noljanolja.server.admin.adapter.core

import com.noljanolja.server.admin.model.VideoRewardConfig

data class CoreVideoRewardConfig(
    val id: Long,
    val videoId: String,
    val isActive: Boolean,
    val maxApplyTimes: Int,
    val rewardProgresses: List<RewardProgress>,
) {
    data class RewardProgress(
        val progress: Double,
        val point: Long,
    )
}

fun CoreVideoRewardConfig.toVideoRewardConfig() = VideoRewardConfig(
    id = id,
    videoId = videoId,
    isActive = isActive,
    maxApplyTimes = maxApplyTimes,
    rewardProgresses = rewardProgresses.map {
        VideoRewardConfig.RewardProgress(
            progress = it.progress,
            point = it.point,
        )
    }
)