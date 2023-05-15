package com.noljanolja.server.admin.rest.request

import com.noljanolja.server.admin.model.RoomType

data class UpsertChatConfigRequest(
    val roomType: RoomType,
    val isActive: Boolean,
    val maxApplyTimes: Int?,
    val rewardPoint: Long,
    val numberOfMessages: Int,
    val onlyRewardCreator: Boolean,
)
