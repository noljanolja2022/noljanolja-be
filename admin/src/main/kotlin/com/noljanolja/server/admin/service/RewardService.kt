package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.adapter.core.CoreChatRewardConfig
import com.noljanolja.server.admin.adapter.core.request.CoreUpsertChatConfigRequest
import com.noljanolja.server.admin.adapter.core.request.CoreUpsertVideoConfigRequest
import com.noljanolja.server.admin.adapter.core.toChatRewardConfig
import com.noljanolja.server.admin.adapter.core.toVideoRewardConfig
import com.noljanolja.server.admin.exception.Error
import com.noljanolja.server.admin.model.RoomType
import com.noljanolja.server.admin.model.VideoRewardConfig
import com.noljanolja.server.admin.rest.request.UpsertChatConfigRequest
import com.noljanolja.server.admin.rest.request.UpsertVideoConfigRequest
import org.springframework.stereotype.Component

@Component
class RewardService(
    private val coreApi: CoreApi,
) {
    suspend fun getVideosRewardConfigs(
        page: Int,
        pageSize: Int,
    ): Pair<List<VideoRewardConfig>, Long> {
        return coreApi.getVideosRewardConfigs(
            page = page,
            pageSize = pageSize,
        ).let { (first, second) ->
            Pair(first.map { it.toVideoRewardConfig() }, second.total)
        }
    }

    suspend fun getVideoRewardConfigs(
        configId: Long,
    ) = coreApi.getVideoRewardConfig(configId).toVideoRewardConfig()

    suspend fun getVideoRewardConfig(
        videoId: String
    ) = coreApi.getVideoRewardConfig(videoId)?.toVideoRewardConfig()

    suspend fun deleteVideoConfig(
        configId: Long,
    ) = coreApi.deleteVideoRewardConfigs(configId)

    suspend fun upsertVideoRewardConfigs(
        request: UpsertVideoConfigRequest,
    ) = coreApi.upsertVideoRewardConfigs(
        CoreUpsertVideoConfigRequest(
            videoId = request.videoId,
            isActive = request.isActive,
            maxApplyTimes = request.maxApplyTimes,
            totalPoints = request.totalPoints,
            rewardProgresses = request.rewardProgresses.sortedBy { it.progress }.let { progresses ->
                var previousProgressPoint = 0L
                progresses.map {
                    if (it.progress < 0 || it.progress > 1) throw Error.InvalidProgress
                    val progress = CoreUpsertVideoConfigRequest.VideoConfigProgress(
                        point = it.point - previousProgressPoint,
                        progress = it.progress,
                    )
                    previousProgressPoint = it.point
                    progress
                }
            }
        )
    ).toVideoRewardConfig()

    suspend fun getChatConfigs(
        roomType: RoomType? = null,
    ) = coreApi.getChatConfigs(roomType?.name).map{it.toChatRewardConfig()}

    suspend fun upsertChatConfig(
        payload: UpsertChatConfigRequest,
    ) = coreApi.upsertChatConfig(
        CoreUpsertChatConfigRequest(
            roomType = CoreChatRewardConfig.RoomType.valueOf(payload.roomType.name),
            isActive = payload.isActive,
            maxApplyTimes = payload.maxApplyTimes,
            rewardPoint = payload.rewardPoint,
            numberOfMessages = payload.numberOfMessages,
            onlyRewardCreator = payload.onlyRewardCreator,
        )
    ).toChatRewardConfig()
}