package com.noljanolja.server.reward.rest.request

data class UpsertCheckinConfigsRequest(
    val configs: List<CheckinConfig>,
) {
    data class CheckinConfig(
        val day: Int,
        val rewardPoints: Long,
    )
}