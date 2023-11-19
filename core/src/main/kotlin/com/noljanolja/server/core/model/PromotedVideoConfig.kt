package com.noljanolja.server.core.model

import java.time.Instant
import java.time.LocalDate

data class PromotedVideoConfig(
    val id: Long = 0,
    val startDate: LocalDate,
    val autoPlay: Boolean,
    val autoLike: Boolean,
    val autoSubscribe: Boolean,
    val autoComment: Boolean,
    val interactionDelay: Int,
    val endDate: LocalDate,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val video: Video?
)