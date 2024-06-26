package com.noljanolja.server.admin.model

data class VideoRewardConfig(
    val id: Long,
    val videoId: String,
    val isActive: Boolean,
    val maxApplyTimes: Int,
    val totalPoints: Long?,
    val rewardProgresses: List<RewardProgress>,
    val minCommentLength: Int = 0,
    val commentMaxApplyTimes: Int = 0,
    val commentRewardPoints: Long = 0,
    val likeMaxApplyTimes: Int = 0,
    val likeRewardPoints: Long = 0,
    val accumulationConfigLog: String? = null
) {
    data class RewardProgress(
        val progress: Double,
        val point: Long,
    )
}
