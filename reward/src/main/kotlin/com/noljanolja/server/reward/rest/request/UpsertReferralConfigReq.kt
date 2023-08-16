package com.noljanolja.server.reward.rest.request

data class UpsertReferralConfigReq(
    val refereePoints: Long = 0,
    val refererPoints: Long = 0,
)