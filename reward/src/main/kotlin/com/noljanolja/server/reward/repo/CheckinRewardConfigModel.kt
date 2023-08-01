package com.noljanolja.server.reward.repo

import com.noljanolja.server.reward.model.CheckinRewardConfig
import com.noljanolja.server.reward.model.UserCheckinProgress
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("checkin_reward_configs")
data class CheckinRewardConfigModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("day")
    val day: Int,

    @Column("reward_points")
    val rewardPoints: Long,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun CheckinRewardConfigModel.toCheckinRewardConfig() = CheckinRewardConfig(
    id = id,
    day = day,
    rewardPoints = rewardPoints,
)