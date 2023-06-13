package com.noljanolja.server.core.rsocket

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
) {
    @MessageMapping("watch-video")
    suspend fun handleUserWatchVideo(request: UserVideoProgress) {
        with(request) {
            coroutineScope {
                launch {
                    videoRewardService.handleRewardUser(
                        userId = userId,
                        videoId = videoId,
                        progressPercentage = progressPercentage,
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
}