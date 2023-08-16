package com.noljanolja.server.reward.service

import com.noljanolja.server.reward.model.ReferralConfig
import com.noljanolja.server.reward.repo.ReferralRewardConfigModel
import com.noljanolja.server.reward.repo.ReferralRewardConfigRepo
import com.noljanolja.server.reward.rest.request.UpsertReferralConfigReq
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ReferralService(
    private val referralRewardConfigRepo: ReferralRewardConfigRepo,
) {
    suspend fun getConfig(): ReferralConfig? {
        val res = referralRewardConfigRepo.findAll().toList().firstOrNull()
        return res?.toReferralConfig()
    }

    suspend fun updateConfig(payload: UpsertReferralConfigReq): ReferralConfig {
        val currentConfig = referralRewardConfigRepo.findAll().toList().firstOrNull() ?: ReferralRewardConfigModel()
        currentConfig.apply {
            rewardPoints = payload.refererPoints
            refereePoints = payload.refereePoints
            updatedAt = Instant.now()
        }
        val res = referralRewardConfigRepo.save(currentConfig)
        return res.toReferralConfig()
    }
}