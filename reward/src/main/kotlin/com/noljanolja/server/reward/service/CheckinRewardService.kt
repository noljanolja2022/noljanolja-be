package com.noljanolja.server.reward.service

import com.noljanolja.server.loyalty.service.LoyaltyService
import com.noljanolja.server.loyalty.service.REASON_DAILY_CHECKIN
import com.noljanolja.server.reward.exception.Error
import com.noljanolja.server.reward.model.CheckinRewardConfig
import com.noljanolja.server.reward.model.UserCheckinProgress
import com.noljanolja.server.reward.repo.*
import com.noljanolja.server.reward.rest.request.UpsertCheckinConfigsRequest
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Component
@Transactional
class CheckinRewardService(
    private val checkinRewardConfigRepo: CheckinRewardConfigRepo,
    private val userCheckinRecordRepo: UserCheckinRecordRepo,
    private val loyaltyService: LoyaltyService,
) {
    suspend fun userCheckin(
        userId: String,
    ): CheckinRewardConfig {
        val configs = checkinRewardConfigRepo.findAll().toList().ifEmpty { throw Error.CheckinConfigNotFound }
        val totalDays = configs.size
        val userLastCheckinRecord = userCheckinRecordRepo.findFirstByUserIdOrderByCreatedAtDesc(userId)
        val checkinRecord = userLastCheckinRecord?.let {
            val diffDays = abs(
                ChronoUnit.DAYS.between(LocalDate.now(), it.createdAt.atZone(ZoneOffset.UTC).toLocalDate()).toInt()
            )
            when {
                diffDays == 0 -> it
                diffDays == 1 && it.day < totalDays -> UserCheckinRecordModel(
                    day = it.day + 1,
                    userId = userId,
                )

                else -> null
            }
        }
            ?: UserCheckinRecordModel(
                day = 1,
                userId = userId,
            )
        val activeConfig = configs.find { it.day == checkinRecord.day } ?: throw Error.CheckinConfigNotFound
        if (checkinRecord.id == 0L) {
            userCheckinRecordRepo.save(checkinRecord)
            loyaltyService.addTransaction(
                memberId = userId,
                points = activeConfig.rewardPoints,
                reason = REASON_DAILY_CHECKIN,
            )
        }
        // next reward config
        return ((configs.find { it.day == activeConfig.day + 1 } ?: configs.minBy { it.day })).toCheckinRewardConfig()
    }

    suspend fun getAll(): List<CheckinRewardConfig> {
        return checkinRewardConfigRepo.findAll().toList().map {
            it.toCheckinRewardConfig()
        }
    }

    suspend fun upsertCheckinConfigs(
        configs: List<UpsertCheckinConfigsRequest.CheckinConfig>,
    ): List<CheckinRewardConfig> {
        checkinRewardConfigRepo.deleteAll()
        return checkinRewardConfigRepo.saveAll(
            configs.map {
                CheckinRewardConfigModel(
                    day = it.day,
                    rewardPoints = it.rewardPoints,
                )
            }
        ).toList().map { it.toCheckinRewardConfig() }
    }

    suspend fun getUserCheckinProgresses(
        userId: String,
        localDate: LocalDate? = null,
    ): List<UserCheckinProgress> {
        val currentDate = localDate ?: LocalDate.now()
        val configs = checkinRewardConfigRepo.findAll().toList()
        val checkinRecords = userCheckinRecordRepo.findAllCheckinRecordsOfUserInMonthYear(
            userId = userId,
            month = currentDate.monthValue,
            year = currentDate.year,
        ).toList()
        return (1..currentDate.lengthOfMonth()).map {
            val dayInMonth = LocalDate.of(currentDate.year, currentDate.month, it)
            val correspondCheckinRecord = checkinRecords.find {
                it.createdAt.atOffset(ZoneOffset.UTC).toLocalDate() == dayInMonth
            }
            UserCheckinProgress(
                rewardPoints = configs.find { it.day == correspondCheckinRecord?.day }?.rewardPoints ?: 0,
                day = dayInMonth,
            )
        }
    }
}