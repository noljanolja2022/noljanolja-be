package com.noljanolja.server.core.repo.media

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("promoted_video_user_logs")
data class PromotedVideoUserLogModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("user_id")
    val userId: String,

    @Column("video_id")
    val videoId: String,

    @Column("channel_id")
    val channelId: String,

    @Column("liked")
    val liked: Boolean,

    @Column("commented")
    val commented: Boolean,

    @Column("subscribed")
    val subscribed: Boolean,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)