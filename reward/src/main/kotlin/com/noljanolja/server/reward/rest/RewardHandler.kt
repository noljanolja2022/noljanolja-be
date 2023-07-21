package com.noljanolja.server.reward.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.reward.repo.RoomType
import com.noljanolja.server.reward.rest.request.UpsertChatConfigRequest
import com.noljanolja.server.reward.rest.request.UpsertCheckinConfigsRequest
import com.noljanolja.server.reward.rest.request.UpsertVideoConfigRequest
import com.noljanolja.server.reward.service.ChatRewardService
import com.noljanolja.server.reward.service.CheckinRewardService
import com.noljanolja.server.reward.service.VideoRewardService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class RewardHandler(
    private val videoRewardService: VideoRewardService,
    private val chatRewardService: ChatRewardService,
    private val checkinRewardService: CheckinRewardService,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun getUserVideosRewards(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val videoIds = request.queryParamOrNull("videoIds").orEmpty()
            .ifBlank { throw InvalidParamsException("videoIds") }
            .split(",")

        val userVideoRewards = videoRewardService.getUserVideosRewards(
            userId = userId,
            videoIds = videoIds.toSet(),
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = userVideoRewards,
                )
            )
    }

    suspend fun getVideosRewardConfigs(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val (configs, total) = videoRewardService.getVideosRewardConfigs(
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
        val video = videoRewardService.getVideoRewardConfigs(
            configId = configId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = video,
                )
            )
    }

    suspend fun getVideoRewardConfigV2(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId")
        val video = videoRewardService.getVideoRewardConfig(
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
        val newConfig = videoRewardService.upsertVideoRewardConfigs(
            newConfig = payload,
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
        videoRewardService.deleteVideoConfig(
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
        val configs = chatRewardService.getChatConfigs(roomType)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = configs,
                )
            )
    }

    suspend fun upsertChatConfig(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpsertChatConfigRequest>() ?: throw RequestBodyRequired
        val newConfig = chatRewardService.upsertChatConfig(payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = newConfig,
                )
            )
    }

    suspend fun getUserCheckinProgresses(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val progresses = checkinRewardService.getUserCheckinProgresses(userId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = progresses,
                )
            )
    }

    suspend fun getCheckinConfigs(request: ServerRequest): ServerResponse {
        val res = checkinRewardService.getAll()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res,
                )
            )
    }

    suspend fun upsertCheckinConfigs(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpsertCheckinConfigsRequest>() ?: throw RequestBodyRequired
        val configs = checkinRewardService.upsertCheckinConfigs(payload.configs)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = configs,
                )
            )
    }

    suspend fun userCheckin(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        checkinRewardService.userCheckin(userId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}