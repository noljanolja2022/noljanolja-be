package com.noljanolja.server.loyalty.repo

import com.noljanolja.server.loyalty.model.MemberInfo
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
    @Column("point")
    val point: Long = 0,
    @Column("current_tier")
    val currentTier: MemberInfo.Tier = MemberInfo.Tier.BRONZE,
    @Column("current_tier_min_point")
    val currentTierMinPoint: Long? = null,
    @Column("next_tier")
    val nextTier: MemberInfo.Tier? = null,
    @Column("next_tier_min_point")
    val nextTierMinPoint: Long? = null,
    @Column("expiry_points")
    val expiryPoints: String = "[]"
) : Persistable<String> {
    @Transient
    var isNewRecord = false

    override fun isNew(): Boolean = isNewRecord

    override fun getId() = memberId

    fun toMemberInfo(): MemberInfo {
        return MemberInfo(
            memberId, point, currentTier, currentTierMinPoint, nextTier, nextTierMinPoint
        )
    }
}