package com.noljanolja.server.consumer.adapter.core

import java.time.Instant

data class CoreUserVideoRewardRecord(
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
