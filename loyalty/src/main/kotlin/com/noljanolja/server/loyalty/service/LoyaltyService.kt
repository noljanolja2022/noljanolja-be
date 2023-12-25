package com.noljanolja.server.loyalty.service

import com.noljanolja.server.loyalty.exception.Error
import com.noljanolja.server.loyalty.model.MemberInfo
import com.noljanolja.server.loyalty.model.Transaction
import com.noljanolja.server.loyalty.repo.*
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.*
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

    private suspend fun createNewMember(memberId: String): MemberInfoModel {
        return memberInfoRepo.save(MemberInfoModel(
            memberId = memberId,
        ).apply { isNewRecord = true })
    }

    suspend fun getStartOfCurrentDay(
        timeZone: TimeZone = KOREA_TIME_ZONE
    ) = Clock.System.todayIn(KOREA_TIME_ZONE).atStartOfDayIn(KOREA_TIME_ZONE).toJavaInstant()

    suspend fun getMember(memberId: String): MemberInfo {
        val member = memberInfoRepo.findById(memberId) ?: createNewMember(memberId)
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
        log: String? = null
    ): Transaction {
        val member = memberInfoRepo.findByMemberIdForUpdate(memberId) ?: createNewMember(memberId)
        if (member.availablePoints + points < 0) throw Error.InsufficientPointBalance
        memberInfoRepo.save(
            MemberInfoModel(
                memberId = memberId,
                availablePoints = member.availablePoints + points,
                accumulatedPoints = member.accumulatedPoints + (points.takeIf { it > 0 } ?: 0),
            )
        )
        return transactionRepo.save(
            TransactionModel(
                memberId = memberId,
                amount = points,
                reason = reason,
                log = log
            )
        ).toTransaction(true)
    }

    suspend fun getTransactions(
        memberId: String,
        lastOffsetDate: JavaInstant? = null,
        type: Transaction.Type? = null,
        month: Int? = null,
        year: Int? = null,
        pageSize: Int = 20,
    ): List<Transaction> {
        val transactions = if (month != null && year != null) {
            transactionRepo.findAllByMemberIdAndMonthYear(
                memberId = memberId,
                month = month,
                year = year
            ).toList()
        } else if (month == null && year == null) {
            transactionRepo.findAllByMemberIdAndCreatedAtIsBeforeOrderByCreatedAtDesc(
                memberId = memberId,
                timestamp = lastOffsetDate ?: JavaInstant.now(),
                limit = pageSize
            ).toList()
        } else {
            emptyList()
        }

        return transactions.filter {
            type == null ||
                    (type == Transaction.Type.RECEIVED && it.amount > 0) ||
                    (type == Transaction.Type.SPENT && it.amount < 0)
        }.map { it.toTransaction(true) }
    }

    suspend fun getTransactionDetails(
        memberId: String,
        transactionId: Long,
        reason: String
    ) : Transaction {
        val transaction = if (reason == "REASON_PURCHASE_GIFT") {
            transactionRepo.findByCoinTransactionIdAndMemberId(
                coinTransactionId = transactionId,
                memberId = memberId
            )
        } else {
            transactionRepo.findByIdAndMemberId(
                transactionId = transactionId,
                memberId = memberId
            )
        } ?: throw Error.TransactionNotFound

        return transaction.toTransaction(false)
    }
}