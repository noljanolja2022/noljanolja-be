package com.noljanolja.server.core.rsocket

import com.noljanolja.server.reward.service.VideoRewardService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Controller


@Controller
class RSocketController(
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
    suspend fun handleUserChat(requester: RSocketRequester) {

    }
}