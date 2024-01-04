package com.noljanolja.server.consumer.model

import java.time.Instant

data class ReferralConfig (
    val refereePoints: Long,
    val refererPoints: Long,
    val updatedAt: Instant
)