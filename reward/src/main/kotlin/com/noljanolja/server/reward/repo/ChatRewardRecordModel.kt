package com.noljanolja.server.reward.repo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("chat_reward_records")
data class ChatRewardRecordModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("user_id")
    val userId: String,

    @Column("conversation_id")
    val conversationId: Long,

    @Column("apply_times")
    var applyTimes: Int,

    @Column("message_count")
    var messageCount: Int,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)
