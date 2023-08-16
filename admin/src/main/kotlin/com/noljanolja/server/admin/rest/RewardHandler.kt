package com.noljanolja.server.admin.rest

import com.noljanolja.server.admin.model.RoomType
import com.noljanolja.server.admin.model.UpsertReferralConfigReq
import com.noljanolja.server.admin.rest.request.UpsertChatConfigRequest
import com.noljanolja.server.admin.rest.request.UpsertCheckinConfigRequest
import com.noljanolja.server.admin.rest.request.UpsertVideoConfigRequest
import com.noljanolja.server.admin.service.RewardService
import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class RewardHandler(
    private val rewardService: RewardService,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun getVideosRewardConfigs(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val (configs, total) = rewardService.getVideosRewardConfigs(
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = configs,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }

    suspend fun getVideoRewardConfig(request: ServerRequest): ServerResponse {
        val configId = request.pathVariable("configId").toLongOrNull() ?: throw InvalidParamsException("configId")
        val video = rewardService.getVideoRewardConfigs(
            configId = configId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = video,
                )
            )
    }

    suspend fun getRewardConfigByVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId")
        val video = rewardService.getVideoRewardConfig(
            videoId = videoId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = video,
                )
            )
    }

    suspend fun upsertVideoRewardConfigs(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpsertVideoConfigRequest>() ?: throw RequestBodyRequired
        val newConfig = rewardService.upsertVideoRewardConfigs(
            request = payload,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = newConfig,
                ),
            )
    }

    suspend fun deleteVideoRewardConfigs(request: ServerRequest): ServerResponse {
        val configId = request.pathVariable("configId").toLongOrNull() ?: throw InvalidParamsException("configId")
        rewardService.deleteVideoConfig(
            configId = configId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getChatConfigs(request: ServerRequest): ServerResponse {
        val roomType = request.queryParamOrNull("roomType")?.let {
            try {
                RoomType.valueOf(it)
            } catch (err: Exception) {
                throw InvalidParamsException("roomType")
            }
        }
        val configs = rewardService.getChatConfigs(roomType)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = configs,
                )
            )
    }

    suspend fun upsertChatConfig(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpsertChatConfigRequest>() ?: throw RequestBodyRequired
        val newConfig = rewardService.upsertChatConfig(payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = newConfig,
                )
            )
    }

    suspend fun getCheckinConfig(request: ServerRequest): ServerResponse {
        val res = rewardService.getCheckinConfig()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res,
                )
            )
    }

    suspend fun updateCheckinConfig(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpsertCheckinConfigRequest>() ?: throw RequestBodyRequired
        val res = rewardService.updateCheckinConfig(payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res,
                )
            )
    }

    suspend fun getReferralConfig(request: ServerRequest): ServerResponse {
        val res = rewardService.getReferralConfig()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res,
                )
            )
    }

    suspend fun upsertReferralConfig(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpsertReferralConfigReq>() ?: throw RequestBodyRequired
        val res = rewardService.upsertReferralConfig(payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res,
                )
            )
    }
}