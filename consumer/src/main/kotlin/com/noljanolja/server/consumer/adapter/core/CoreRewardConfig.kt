package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.RewardConfig

data class CoreRewardConfig(
    val rewardPoints: Long = 0,
)

fun CoreRewardConfig.toRewardConfig() = RewardConfig(
    rewardPoints = rewardPoints,
)
