package com.noljanolja.server.reward.model

data class VideoRewardConfig(
    val id: Long,
    val videoId: String,
    val isActive: Boolean,
    val maxApplyTimes: Int,
    val totalPoints: Long?,
    val rewardProgresses: List<RewardProgress>,
    val minCommentLength: Int,
    val commentMaxApplyTimes: Int,
    val commentRewardPoints: Long,
    val likeMaxApplyTimes: Int,
    val likeRewardPoints: Long,
) {
    data class RewardProgress(
        val progress: Double,
        val point: Long,
    )
}