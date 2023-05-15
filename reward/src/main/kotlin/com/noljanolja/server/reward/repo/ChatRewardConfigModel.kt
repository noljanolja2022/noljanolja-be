package com.noljanolja.server.reward.repo

import com.noljanolja.server.reward.model.ChatRewardConfig
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("chat_reward_configs")
data class ChatRewardConfigModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("number_of_messages")
    var numberOfMessages: Int = 0,

    @Column("reward_point")
    var rewardPoint: Long = 0,

    @Column("max_apply_times")
    var maxApplyTimes: Int? = null,

    @Column("active")
    var isActive: Boolean = false,

    @Column("only_reward_creator")
    var onlyRewardCreator: Boolean = false,

    @Column("room_type")
    var roomType: RoomType = RoomType.SINGLE,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun ChatRewardConfigModel.toChatRewardConfig() = ChatRewardConfig(
    id = id,
    numberOfMessages = numberOfMessages,
    rewardPoint = rewardPoint,
    maxApplyTimes = maxApplyTimes,
    isActive = isActive,
    onlyRewardCreator = onlyRewardCreator,
    roomType = roomType,
)

enum class RoomType {
    SINGLE,
    GROUP,
}