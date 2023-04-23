package com.noljanolja.server.loyalty.repo

import com.noljanolja.server.loyalty.model.MemberInfo
import com.noljanolja.server.loyalty.model.TierConfig
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("tier_configs")
data class TierConfigModel(
    @Id
    @Column("id")
    val id: Long,

    @Column("tier")
    val tier: MemberInfo.Tier,

    @Column("min_point")
    val minPoint: Long,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun TierConfigModel.toTierConfig() = TierConfig(
    tier = tier,
    minPoint = minPoint,
)