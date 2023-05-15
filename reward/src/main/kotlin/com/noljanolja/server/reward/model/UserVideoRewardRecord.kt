package com.noljanolja.server.reward.model

import java.time.Instant

data class UserVideoRewardRecord(
    val videoId: String,
    val rewardProgresses: List<RewardProgress>,
    val completed: Boolean,
) {
    data class RewardProgress(
        val progress: Double,
        val point: Long,
        val claimedAts: List<Instant>,
        val completed: Boolean,
    )
}
