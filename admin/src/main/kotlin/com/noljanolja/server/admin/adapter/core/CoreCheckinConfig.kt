package com.noljanolja.server.admin.adapter.core

import com.noljanolja.server.admin.model.CheckinConfig

data class CoreCheckinConfig(
    val day: Int,
    val rewardPoints: Long,
) {
    fun toCheckinConfig() : CheckinConfig {
        return CheckinConfig(
            day, rewardPoints
        )
    }
}