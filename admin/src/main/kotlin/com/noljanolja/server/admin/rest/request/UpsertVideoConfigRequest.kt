package com.noljanolja.server.admin.rest.request

data class UpsertVideoConfigRequest(
    val videoId: String,
    val isActive: Boolean,
    val maxApplyTimes: Int? = null,
    val rewardProgresses: List<VideoConfigProgress>,
) {
    data class VideoConfigProgress(
        val progress: Double,
        val point: Long,
    )
}