package com.noljanolja.server.admin.adapter.core.request

import com.noljanolja.server.admin.adapter.core.CoreChatRewardConfig

data class CoreUpsertChatConfigRequest(
    val roomType: CoreChatRewardConfig.RoomType,
    val isActive: Boolean,
    val maxApplyTimes: Int?,
    val rewardPoint: Long,
    val numberOfMessages: Int,
    val onlyRewardCreator: Boolean,
)
