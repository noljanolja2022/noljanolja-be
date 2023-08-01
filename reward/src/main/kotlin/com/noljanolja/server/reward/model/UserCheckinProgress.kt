package com.noljanolja.server.reward.model

import java.time.LocalDate

data class UserCheckinProgress(
    val day: LocalDate,
    val rewardPoints: Long,
)
