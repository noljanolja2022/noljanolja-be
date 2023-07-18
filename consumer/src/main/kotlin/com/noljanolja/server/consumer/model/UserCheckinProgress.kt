package com.noljanolja.server.consumer.model

data class UserCheckinProgress(
    val id: Long,
    val rewardPoints: Long,
    val day: Int,
    val isCompleted: Boolean,
)
