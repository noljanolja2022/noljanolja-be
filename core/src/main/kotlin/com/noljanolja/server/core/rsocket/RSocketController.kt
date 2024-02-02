package com.noljanolja.server.core.rsocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.repo.media.VideoRepo
import com.noljanolja.server.core.repo.media.toVideoLogTransaction
import com.noljanolja.server.core.service.VideoService
import com.noljanolja.server.reward.service.ChatRewardService
import com.noljanolja.server.reward.service.VideoRewardService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller


@Controller
class RSocketController(
    private val chatRewardService: ChatRewardService,
    private val videoRewardService: VideoRewardService,
    private val videoRepo: VideoRepo,
    private val objectMapper: ObjectMapper,
    private val videoService: VideoService
) {
    @MessageMapping("watch-video")
    suspend fun handleUserWatchVideo(request: UserVideoProgress) {
        with(request) {
            coroutineScope {
                launch {
                    //TODO: switch to admin config
                    if (progressPercentage >= 0.9) {
                        videoService.viewVideo(videoId)
                    }

                    val video = videoRepo.findById(videoId)
                        ?: throw Error.VideoNotFound
                    val videoLogTransaction = video.toVideoLogTransaction(progressPercentage)

                    videoRewardService.handleRewardUserWatchVideo(
                        userId = userId,
                        videoId = videoId,
                        progressPercentage = progressPercentage,
                        log = objectMapper.writeValueAsString(videoLogTransaction)
                    )
                }
            }
        }
    }

    @MessageMapping("send-message")
    suspend fun handleUserChat(request: UserSendChatMessage) {
        with(request) {
            chatRewardService.handleRewardUser(
                userId = userId,
                conversationId = conversationId,
                roomType = roomType,
                creatorId = creatorId,
            )
        }
    }

    @MessageMapping("like-video")
    suspend fun handleUserLikeVideo(request: UserVideoLike) {
        with(request) {
            videoRewardService.handleRewardUserLikeVideo(
                videoId = videoId,
                userId = userId,
            )
        }
    }

    @MessageMapping("comment-video")
    suspend fun handleUserCommentVideo(request: UserVideoComment) {
        with(request) {
            videoRewardService.handleRewardUserCommentVideo(
                comment = comment,
                videoId = videoId,
                userId = userId,
            )
        }
    }
}