package com.noljanolja.server.consumer.model

data class RewardConfig(
    val rewardPoints: Long = 0,
)


data class RewardInfo(
    val balance: Long,
    val giftCount : Int
)