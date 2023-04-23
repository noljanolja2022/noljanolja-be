package com.noljanolja.server.loyalty.repo

import com.noljanolja.server.loyalty.model.MemberInfo
import com.noljanolja.server.loyalty.model.TierConfig
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("member_info")
class MemberInfoModel(
    @Id
    @Column("id")
    val memberId: String,

    @Column("accumulated_points")
    val accumulatedPoints: Long = 0,

    @Column("available_points")
    var availablePoints: Long = 0,

    ) : Persistable<String> {
    @Transient
    var isNewRecord = false

    override fun isNew(): Boolean = isNewRecord

    override fun getId() = memberId


    fun toMemberInfo(
        tiers: List<TierConfig>,
    ): MemberInfo {
        var currentTier = TierConfig()
        var nextTier: TierConfig? = null
        for ((index, value) in tiers.withIndex()) {
            if (accumulatedPoints >= value.minPoint) {
                currentTier = value
                nextTier = if (index < tiers.size - 1) tiers[index + 1] else null
            }
        }
        return MemberInfo(
            memberId = memberId,
            currentTier = currentTier.tier,
            currentTierMinPoint = currentTier.minPoint,
            nextTier = nextTier?.tier,
            nextTierMinPoint = nextTier?.minPoint,
            point = availablePoints,
        )
    }
}