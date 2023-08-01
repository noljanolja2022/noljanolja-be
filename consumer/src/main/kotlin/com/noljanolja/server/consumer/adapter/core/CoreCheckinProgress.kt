package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.UserCheckinProgress
import java.time.LocalDate

data class CoreCheckinProgress(
    val day: LocalDate,
    val rewardPoints: Long,
)

fun CoreCheckinProgress.toCheckinProgress() = UserCheckinProgress(
    day = day,
    rewardPoints = rewardPoints,
)
