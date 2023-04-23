package com.noljanolja.server.loyalty.scheduled

import com.noljanolja.server.loyalty.repo.MemberInfoRepo
import com.noljanolja.server.loyalty.repo.TransactionRepo
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Component
@EnableScheduling
class ScheduledTasks(
    private val memberInfoRepo: MemberInfoRepo,
    private val transactionRepo: TransactionRepo,
) {
    private val logger = LoggerFactory.getLogger(ScheduledTasks::class.java)

    companion object {
        const val EXPIRY_DURATION = 365L * 86400 // seconds
    }

    @Scheduled(cron = "0 0 11 * * ?")
    suspend fun updateUsersBalance() {
        val start = Instant.now().toEpochMilli()
        logger.info("Begin updating users balance")
        memberInfoRepo.findAll().onEach {
            handleUpdatingUserBalance(it.id)
        }
        logger.info("Finished updating users balance in: ${Instant.now().toEpochMilli() - start} milliseconds")
    }

    @Transactional
    suspend fun handleUpdatingUserBalance(memberId: String) {
        memberInfoRepo.findByMemberIdForUpdate(memberId)?.let { member ->
            val transactions = transactionRepo.findAllByMemberIdAndCreatedAtIsAfterOrderByCreatedAtAsc(
                memberId = member.id,
                expireTimestamp = Instant.now().minusSeconds(EXPIRY_DURATION)
            ).toList()
            if (transactions.isNotEmpty()) {
                var shouldIgnoreTransaction = true
                var availableBalance = 0L
                transactions.forEach {
                    if (shouldIgnoreTransaction && it.amount > 0) shouldIgnoreTransaction = false
                    if (!shouldIgnoreTransaction) availableBalance += it.amount
                }
                memberInfoRepo.save(
                    member.apply {
                        this.availablePoints = availableBalance
                    }
                )
            }
        }
    }
}