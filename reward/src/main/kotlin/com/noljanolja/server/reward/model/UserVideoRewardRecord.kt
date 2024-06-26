package com.noljanolja.server.reward.model

import java.time.Instant

data class UserVideoRewardRecord(
    val videoId: String,
    val rewardProgresses: List<RewardProgress>,
    val completed: Boolean,
    val earnedPoints: Long,
    val totalPoints: Long,
) {
    data class RewardProgress(
        val progress: Double,
        val point: Long,
        val claimedAts: List<Instant>,
        val completed: Boolean,
    )
}
