package com.noljanolja.server.reward.repo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("video_comment_reward_records")
data class VideoCommentRewardRecordModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("config_id")
    val configId: Long,

    @Column("user_id")
    val userId: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)
