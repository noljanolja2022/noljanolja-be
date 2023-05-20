package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toConsumerLoyaltyPoint
import com.noljanolja.server.consumer.adapter.core.toConsumerMemberInfo
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class LoyaltyService(
    private val coreApi: CoreApi,
) {
    suspend fun getMemberInfo(
        userId: String,
    ) = coreApi.getMemberInfo(userId).toConsumerMemberInfo()

    suspend fun getLoyaltyPoints(
        userId: String,
        lastOffsetDate: Instant? = null,
        type: String? = null,
        month: Int? = null,
        year: Int? = null,
    ) = coreApi.getLoyaltyPoints(
        userId = userId,
        lastOffsetDate = lastOffsetDate,
        type = type,
        month = month,
        year = year,
    ).map { it.toConsumerLoyaltyPoint() }
}