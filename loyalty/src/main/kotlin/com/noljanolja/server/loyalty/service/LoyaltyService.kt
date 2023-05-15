package com.noljanolja.server.loyalty.service

import com.noljanolja.server.loyalty.exception.Error
import com.noljanolja.server.loyalty.model.MemberInfo
import com.noljanolja.server.loyalty.model.Transaction
import com.noljanolja.server.loyalty.repo.*
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class LoyaltyService(
    private val memberInfoRepo: MemberInfoRepo,
    private val transactionRepo: TransactionRepo,
    private val tierConfigRepo: TierConfigRepo,
) {
    suspend fun getMember(memberId: String): MemberInfo {
        val member = memberInfoRepo.findById(memberId) ?: throw Error.MemberNotFound
        val tiers = tierConfigRepo.findAllByOrderByMinPointAsc().toList().map { it.toTierConfig() }
        return member.toMemberInfo(tiers)
    }

    suspend fun upsertMember(memberId: String): MemberInfo {
        val savedMember = memberInfoRepo.save(
            memberInfoRepo.findById(memberId) ?: MemberInfoModel(
                memberId = memberId,
            ).apply { isNewRecord = true }
        )
        val tiers = tierConfigRepo.findAllByOrderByMinPointAsc().toList().map { it.toTierConfig() }
        return savedMember.toMemberInfo(tiers)
    }

    suspend fun addTransaction(
        memberId: String,
        point: Long,
        reason: String,
    ): Transaction {
        memberInfoRepo.findByMemberIdForUpdate(memberId)?.let {
            if (it.availablePoints + point < 0) throw Error.InsufficientBalance
            memberInfoRepo.save(
                MemberInfoModel(
                    memberId = memberId,
                    availablePoints = it.availablePoints + point,
                    accumulatedPoints = it.accumulatedPoints + (point.takeIf { it > 0 } ?: 0),
                )
            )
        } ?: throw Error.MemberNotFound
        return transactionRepo.save(
            TransactionModel(
                memberId = memberId,
                amount = point,
                reason = reason,
            )
        ).toTransaction()
    }

    suspend fun getTransactions(
        memberId: String,
        page: Int,
        pageSize: Int,
    ): Pair<List<Transaction>, Long> {
        return Pair(
            transactionRepo.findAllByMemberIdOrderByCreatedAtDesc(
                memberId = memberId,
                pageable = PageRequest.of(page - 1, pageSize)
            ).toList().map { it.toTransaction() },
            transactionRepo.countByMemberId(memberId),
        )
    }
}