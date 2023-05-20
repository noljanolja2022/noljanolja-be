package com.noljanolja.server.consumer.model

import java.time.Instant

data class MemberInfo(
    val memberId: String,
    val point: Long = 0,
    val currentTier: Tier = Tier.BRONZE,
    val currentTierMinPoint: Long?,
    val nextTier: Tier? = null,
    val nextTierMinPoint: Long?,
    val expiryPoints: List<ExpiryPoint> = emptyList(),
    val accumulatedPointsToday: Long,
    val exchangeablePoints: Long,
) {
    enum class Tier {
        BRONZE, SILVER, GOLD, DIAMOND
    }

    data class ExpiryPoint(
        val point: Long,
        val date: Instant
    )
}
