package com.noljanolja.server.consumer.model

import java.time.LocalDate

data class UserCheckinProgress(
    val rewardPoints: Long,
    val day: LocalDate,
)
