package com.noljanolja.server.reward.model

import com.noljanolja.server.reward.repo.RoomType

data class ChatRewardConfig(
    val id: Long,
    val roomType: RoomType,
    val isActive: Boolean,
    val maxApplyTimes: Int,
    val onlyRewardCreator: Boolean,
    val rewardPoint: Long,
    val numberOfMessages: Int,
)
