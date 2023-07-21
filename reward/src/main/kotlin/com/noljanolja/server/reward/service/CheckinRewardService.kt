package com.noljanolja.server.reward.service

import com.noljanolja.server.loyalty.service.LoyaltyService
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
    ) {
        val configs = checkinRewardConfigRepo.findAll().toList().ifEmpty { return }
        val totalDays = configs.size
        val userLastCheckinRecord = userCheckinRecordRepo.findFirstByUserIdOrderByCreatedAtDesc(userId)
        val checkinRecord = userLastCheckinRecord?.let {
            val diffDays = abs(
                ChronoUnit.DAYS.between(LocalDate.now(), it.createdAt.atZone(ZoneOffset.UTC).toLocalDate()).toInt()
            )
            when {
                diffDays == 0 -> return
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
        val activeConfig = configs.find { it.day == checkinRecord.day } ?: return
        userCheckinRecordRepo.save(checkinRecord)
        loyaltyService.addTransaction(
            memberId = userId,
            points = activeConfig.rewardPoints,
            reason = "Daily checkin",
        )
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
    ): List<UserCheckinProgress> {
        val configs = checkinRewardConfigRepo.findAll().toList().ifEmpty { return emptyList() }
        val totalDays = configs.size
        val userLastCheckinRecord = userCheckinRecordRepo.findFirstByUserIdOrderByCreatedAtDesc(userId)
        val userCheckinRecords = userLastCheckinRecord?.let {
            val diffDays = abs(
                ChronoUnit.DAYS.between(LocalDate.now(), it.createdAt.atZone(ZoneOffset.UTC).toLocalDate()).toInt()
            )
            when {
                diffDays == 0 || (diffDays == 1 && it.day < totalDays)
                -> userCheckinRecordRepo.findActiveCheckinRecords(userId).toList()

                else -> emptyList()
            }
        }.orEmpty()
        return configs.toUserCheckinProgresses(userCheckinRecords)
    }
}