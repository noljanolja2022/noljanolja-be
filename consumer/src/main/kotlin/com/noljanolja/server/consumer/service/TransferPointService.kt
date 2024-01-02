package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toUserTransferPoint
import com.noljanolja.server.consumer.model.UserTransferPoint
import org.springframework.stereotype.Component

@Component
class TransferPointService(
    private val coreApi: CoreApi
) {
    suspend fun requestPoint(
        fromUserId: String,
        toUserId: String,
        points: Long
    ): UserTransferPoint {
        return coreApi.requestPoint(
            fromUserId = fromUserId,
            toUserId = toUserId,
            points = points
        ).toUserTransferPoint()
    }

    suspend fun sendPoint(
        fromUserId: String,
        toUserId: String,
        points: Long
    ): UserTransferPoint {
        return coreApi.sendPoint(
            fromUserId = fromUserId,
            toUserId = toUserId,
            points = points
        ).toUserTransferPoint()
    }
}