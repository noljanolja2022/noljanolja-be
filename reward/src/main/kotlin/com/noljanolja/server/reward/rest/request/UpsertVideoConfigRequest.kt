package com.noljanolja.server.reward.rest.request

data class UpsertVideoConfigRequest(
    val videoId: String,
    val isActive: Boolean,
    val totalPoints: Long?,
    val maxApplyTimes: Int,
    val rewardProgresses: List<VideoConfigProgress>,
    val minCommentLength: Int = 0,
    val commentMaxApplyTimes: Int = 0,
    val commentRewardPoints: Long = 0,
    val likeMaxApplyTimes: Int = 0,
    val likeRewardPoints: Long = 0,
    val accumulationConfigLog: String? = null
) {
    data class VideoConfigProgress(
        val progress: Double,
        val point: Long,
    )
}
