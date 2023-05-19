package com.noljanolja.server.reward.rest.request

data class UpsertVideoConfigRequest(
    val videoId: String,
    val isActive: Boolean,
    val maxApplyTimes: Int,
    val rewardProgresses: List<VideoConfigProgress>,
) {
    data class VideoConfigProgress(
        val progress: Double,
        val point: Long,
    )
}
