package com.noljanolja.server.loyalty.service

import com.noljanolja.server.loyalty.exception.Error
import com.noljanolja.server.loyalty.model.MemberInfo
import com.noljanolja.server.loyalty.model.Transaction
import com.noljanolja.server.loyalty.repo.*
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.*
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant as JavaInstant

@Component
@Transactional
class LoyaltyService(
    private val memberInfoRepo: MemberInfoRepo,
    private val transactionRepo: TransactionRepo,
    private val tierConfigRepo: TierConfigRepo,
) {
    companion object {
        val KOREA_TIME_ZONE = TimeZone.of("UTC+9")
    }

    suspend fun getStartOfCurrentDay(
        timeZone: TimeZone = KOREA_TIME_ZONE
    ) = Clock.System.todayIn(KOREA_TIME_ZONE).atStartOfDayIn(KOREA_TIME_ZONE).toJavaInstant()

    suspend fun getMember(memberId: String): MemberInfo {
        val member = memberInfoRepo.findById(memberId) ?: throw Error.MemberNotFound
        val tiers = tierConfigRepo.findAllByOrderByMinPointAsc().toList().map { it.toTierConfig() }
        val todayTransactions = transactionRepo.findAllByMemberIdAndCreatedAtIsAfterOrderByCreatedAtAsc(
            memberId = memberId,
            timestamp = getStartOfCurrentDay(),
        ).toList()
        return member.apply {
            accumulatedPointsToday = todayTransactions.sumOf { if (it.amount > 0) it.amount else 0 }
            exchangeablePoints = this.availablePoints
        }.toMemberInfo(tiers)
    }

    suspend fun upsertMember(memberId: String): MemberInfo {
        val savedMember = memberInfoRepo.save(
            memberInfoRepo.findById(memberId) ?: MemberInfoModel(
                memberId = memberId,
            ).apply { isNewRecord = true }
        )
        val todayTransactions = transactionRepo.findAllByMemberIdAndCreatedAtIsAfterOrderByCreatedAtAsc(
            memberId = memberId,
            timestamp = getStartOfCurrentDay(),
        ).toList()
        val tiers = tierConfigRepo.findAllByOrderByMinPointAsc().toList().map { it.toTierConfig() }
        return savedMember.apply {
            accumulatedPointsToday = todayTransactions.sumOf { if (it.amount > 0) it.amount else 0 }
            exchangeablePoints = this.availablePoints
        }.toMemberInfo(tiers)
    }

    suspend fun addTransaction(
        memberId: String,
        points: Long,
        reason: String,
    ): Transaction {
        memberInfoRepo.findByMemberIdForUpdate(memberId)?.let {
            if (it.availablePoints + points < 0) throw Error.InsufficientBalance
            memberInfoRepo.save(
                MemberInfoModel(
                    memberId = memberId,
                    availablePoints = it.availablePoints + points,
                    accumulatedPoints = it.accumulatedPoints + (points.takeIf { it > 0 } ?: 0),
                )
            )
        } ?: throw Error.MemberNotFound
        return transactionRepo.save(
            TransactionModel(
                memberId = memberId,
                amount = points,
                reason = reason,
            )
        ).toTransaction()
    }

    suspend fun getTransactions(
        memberId: String,
        lastOffsetDate: JavaInstant? = null,
        type: Transaction.Type? = null,
        month: Int? = null,
        year: Int? = null,
        pageSize: Int = 20,
    ): List<Transaction> {
        val transactions = if (listOfNotNull(lastOffsetDate, month, year).isEmpty() || lastOffsetDate != null) {
            transactionRepo.findAllByMemberIdAndCreatedAtIsBeforeOrderByCreatedAtDesc(
                memberId = memberId,
                timestamp = lastOffsetDate ?: JavaInstant.now(),
                pageable = Pageable.ofSize(pageSize)
            ).toList()
        } else if (month != null && year != null) {
            transactionRepo.findAllByMemberIdAndMonthYear(
                memberId = memberId,
                month = month,
                year = year,
            ).toList()
        } else {
            emptyList()
        }
        return transactions.filter {
            type == null ||
                    (type == Transaction.Type.RECEIVED && it.amount > 0) ||
                    (type == Transaction.Type.SPENT && it.amount < 0)
        }.map { it.toTransaction() }
    }
}