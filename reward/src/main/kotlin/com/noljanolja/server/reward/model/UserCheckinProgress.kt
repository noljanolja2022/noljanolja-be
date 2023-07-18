package com.noljanolja.server.reward.model

data class UserCheckinProgress(
    val id: Long,
    val day: Int,
    val rewardPoints: Long,
    val isCompleted: Boolean,
)
