package com.noljanolja.server.admin.adapter.core

import com.noljanolja.server.admin.model.VideoRewardConfig

data class CoreVideoRewardConfig(
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
    totalPoints = totalPoints,
    rewardProgresses = rewardProgresses.let { progresses ->
        var previousPoints = 0L
        progresses.map {
            val progress = VideoRewardConfig.RewardProgress(
                progress = it.progress,
                point = it.point + previousPoints,
            )
            previousPoints += it.point
            progress
        }
    },
    minCommentLength = minCommentLength,
    commentMaxApplyTimes = commentMaxApplyTimes,
    commentRewardPoints = commentRewardPoints,
    likeMaxApplyTimes = likeMaxApplyTimes,
    likeRewardPoints = likeRewardPoints,
)