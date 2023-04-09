package com.noljanolja.server.loyalty.service

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.loyalty.model.MemberInfo
import com.noljanolja.server.loyalty.repo.MemberInfoModel
import com.noljanolja.server.loyalty.repo.MemberInfoRepo
import org.springframework.context.annotation.Configuration

@Configuration
class LoyaltyService(
    private val memberInfoRepo: MemberInfoRepo
) {
    suspend fun getMember(memberId: String): MemberInfo {
        val member =
            memberInfoRepo.findById(memberId) ?: throw DefaultBadRequestException(Exception("MemberId not found"))
        return member.toMemberInfo()
    }

    suspend fun upsertMember(memberId: String): MemberInfo {
        val newMember = memberInfoRepo.findById(memberId) ?: MemberInfoModel(
            memberId = memberId,
        ).apply { isNewRecord = true }
        val savedMember = memberInfoRepo.save(newMember)
        return savedMember.toMemberInfo()
    }
}