package com.noljanolja.server.reward.repo

import com.noljanolja.server.reward.model.ReferralConfig
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("referral_reward_configs")
data class ReferralRewardConfigModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("reward_points")
    var rewardPoints: Long = 0,

    @Column("referee_points")
    var refereePoints: Long = 0,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
) {
    fun toReferralConfig() : ReferralConfig {
        return ReferralConfig(
            refereePoints, rewardPoints, updatedAt
        )
    }
}
