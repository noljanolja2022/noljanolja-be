package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.CoreReferralConfig
import com.noljanolja.server.consumer.adapter.core.toReferralConfig
import com.noljanolja.server.consumer.model.ReferralConfig
import org.springframework.stereotype.Component

@Component
class ReferralService (
    private val coreApi: CoreApi
) {
    suspend fun getConfig(): ReferralConfig {
        val coreReferralConfig = coreApi.getReferralConfig().data ?: CoreReferralConfig()
        return coreReferralConfig.toReferralConfig()
    }
}