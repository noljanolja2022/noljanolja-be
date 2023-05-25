package com.noljanolja.server.core.rsocket

import com.noljanolja.server.reward.service.ChatRewardService
import com.noljanolja.server.reward.service.VideoRewardService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller


@Controller
class RSocketController(
    private val chatRewardService: ChatRewardService,
    private val videoRewardService: VideoRewardService,
) {
    @MessageMapping("watch-video")
    suspend fun handleUserWatchVideo(request: UserVideoProgress) {
        with(request) {
            videoRewardService.handleRewardUser(
                userId = userId,
                videoId = videoId,
                sessionId = sessionId,
                progressPercentage = progressPercentage,
            )
        }
    }

    @MessageMapping("send-message")
    suspend fun handleUserChat(request: UserSendChatMessage) {
        with(request) {
            chatRewardService.handleRewardUser(
                userId = userId,
                conversationId = conversationId,
                roomType = roomType,
            )
        }
    }
}