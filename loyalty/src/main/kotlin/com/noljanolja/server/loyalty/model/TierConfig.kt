package com.noljanolja.server.loyalty.model

data class TierConfig(
    val tier: MemberInfo.Tier = MemberInfo.Tier.BRONZE,
    val minPoint: Long = 0,
)
