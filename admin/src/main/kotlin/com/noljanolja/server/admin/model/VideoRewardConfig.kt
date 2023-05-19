package com.noljanolja.server.admin.model

data class VideoRewardConfig(
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
