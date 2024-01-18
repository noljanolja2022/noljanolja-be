package com.noljanolja.server.reward.model

import java.time.LocalDateTime

data class PercentageAccumulation (
    val startTime: LocalDateTime,
    val percentage: Long
)