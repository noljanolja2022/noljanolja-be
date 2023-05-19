package com.noljanolja.server.admin.adapter.core.request

data class CoreUpsertVideoConfigRequest(
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

