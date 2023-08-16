package com.noljanolja.server.admin.model

import java.time.Instant

data class ReferralConfig(
    val refereePoints: Long = 0,
    val refererPoints: Long = 0,
    val updatedAt: Instant = Instant.now(),
)

data class UpsertReferralConfigReq(
    val refereePoints: Long = 0,
    val refererPoints: Long = 0,
)