package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.UserCheckinProgress

data class CoreCheckinProgress(
    val id: Long,
    val day: Int,
    val rewardPoints: Long,
    val isCompleted: Boolean,
)

fun CoreCheckinProgress.toCheckinProgress() = UserCheckinProgress(
    id = id,
    day = day,
    rewardPoints = rewardPoints,
    isCompleted = isCompleted,
)
