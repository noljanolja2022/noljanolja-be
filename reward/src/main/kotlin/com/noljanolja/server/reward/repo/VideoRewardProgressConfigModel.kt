package com.noljanolja.server.reward.repo

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("video_reward_progresses_configs")
data class VideoRewardProgressConfigModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("config_id")
    val configId: Long,

    @Column("progress")
    val progress: Double,

    @Column("reward_point")
    val rewardPoint: Long,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),
)