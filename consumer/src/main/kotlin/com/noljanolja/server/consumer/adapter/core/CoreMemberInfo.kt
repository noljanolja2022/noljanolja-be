package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.MemberInfo
import java.time.Instant

data class CoreMemberInfo(
    val memberId: String,
    val point: Long = 0,
    val currentTier: Tier = Tier.BRONZE,
    val currentTierMinPoint: Long?,
    val nextTier: Tier? = null,
    val nextTierMinPoint: Long?,
    val expiryPoints: List<ExpiryPoint> = emptyList()
) {
    enum class Tier {
        BRONZE, SILVER, GOLD, DIAMOND
    }

    data class ExpiryPoint(
        val point: Long,
        val date: Instant
    )
}

fun CoreMemberInfo.toConsumerMemberInfo() = MemberInfo(
    memberId = memberId,
    point = point,
    currentTier = MemberInfo.Tier.valueOf(currentTier.name),
    currentTierMinPoint = currentTierMinPoint,
    nextTier = nextTier?.name?.let { MemberInfo.Tier.valueOf(it) },
    nextTierMinPoint = nextTierMinPoint,
)

