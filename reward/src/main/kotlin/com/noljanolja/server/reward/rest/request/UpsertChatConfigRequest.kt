package com.noljanolja.server.reward.rest.request

import com.noljanolja.server.reward.repo.RoomType

data class UpsertChatConfigRequest(
    val roomType: RoomType,
    val isActive: Boolean,
    val maxApplyTimes: Int?,
    val rewardPoint: Long,
    val numberOfMessages: Int,
    val onlyRewardCreator: Boolean,
)
