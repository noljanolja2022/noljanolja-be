package com.noljanolja.server.reward.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class PercentageAccumulation (
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    val startTime: LocalDateTime,
    val percentage: Long
)