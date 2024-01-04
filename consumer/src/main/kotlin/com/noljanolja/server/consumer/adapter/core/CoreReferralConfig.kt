package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.ReferralConfig
import java.time.Instant

data class CoreReferralConfig(
    val refereePoints: Long = 0,
    val refererPoints: Long = 0,
    val updatedAt: Instant = Instant.now()
)

fun CoreReferralConfig.toReferralConfig() = ReferralConfig(
    refereePoints = refereePoints,
    refererPoints = refererPoints,
    updatedAt = updatedAt
)