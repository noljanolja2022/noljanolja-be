package com.noljanolja.server.admin.model

data class ChatRewardConfig(
    val id: Long,
    val roomType: RoomType,
    val isActive: Boolean,
    val maxApplyTimes: Int?,
    val onlyRewardCreator: Boolean,
    val rewardPoint: Long,
    val numberOfMessages: Int,
)

enum class RoomType {
    SINGLE,
    GROUP
}