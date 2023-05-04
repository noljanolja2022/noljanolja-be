package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toConsumerLoyaltyPoint
import com.noljanolja.server.consumer.adapter.core.toConsumerMemberInfo
import org.springframework.stereotype.Component

@Component
class LoyaltyService(
    private val coreApi: CoreApi,
) {
    suspend fun getMemberInfo(
        userId: String,
    ) = coreApi.getMemberInfo(userId).toConsumerMemberInfo()

    suspend fun getLoyaltyPoints(
        userId: String,
        page: Int,
        pageSize: Int,
    ) = coreApi.getLoyaltyPoints(
        userId = userId,
        page = page,
        pageSize = pageSize,
    ).let {
        Pair(it.first.map { it.toConsumerLoyaltyPoint() }, it.second)
    }
}