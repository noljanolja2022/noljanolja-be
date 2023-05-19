package com.noljanolja.server.admin.adapter.core

import com.noljanolja.server.admin.model.ChatRewardConfig
import com.noljanolja.server.admin.model.RoomType

data class CoreChatRewardConfig(
    val id: Long,
    val roomType: RoomType,
    val isActive: Boolean,
    val maxApplyTimes: Int,
    val onlyRewardCreator: Boolean,
    val rewardPoint: Long,
    val numberOfMessages: Int,
) {
    enum class RoomType {
        SINGLE,
        GROUP
    }
}

fun CoreChatRewardConfig.toChatRewardConfig() = ChatRewardConfig(
    id = id,
    roomType = RoomType.valueOf(roomType.name),
    isActive = isActive,
    maxApplyTimes = maxApplyTimes,
    onlyRewardCreator = onlyRewardCreator,
    rewardPoint = rewardPoint,
    numberOfMessages = numberOfMessages,
)

