package com.noljanolja.server.reward.service

import com.noljanolja.server.reward.model.ChatRewardConfig
import com.noljanolja.server.reward.repo.ChatRewardConfigModel
import com.noljanolja.server.reward.repo.ChatRewardConfigRepo
import com.noljanolja.server.reward.repo.RoomType
import com.noljanolja.server.reward.repo.toChatRewardConfig
import com.noljanolja.server.reward.rest.request.UpsertChatConfigRequest
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component

@Component
class ChatRewardService(
    private val chatRewardConfigRepo: ChatRewardConfigRepo,
) {
    suspend fun upsertChatConfig(
        payload: UpsertChatConfigRequest,
    ): ChatRewardConfig {
        return chatRewardConfigRepo.save(
            (chatRewardConfigRepo.findByRoomType(payload.roomType) ?: ChatRewardConfigModel()).apply {
                numberOfMessages = payload.numberOfMessages
                rewardPoint = payload.rewardPoint
                maxApplyTimes = payload.maxApplyTimes
                isActive = payload.isActive
                onlyRewardCreator = payload.onlyRewardCreator
                roomType = payload.roomType
            }
        ).toChatRewardConfig()
    }

    suspend fun getChatConfigs(
        roomType: RoomType? = null,
    ): List<ChatRewardConfig> {
        return (if (roomType == null) chatRewardConfigRepo.findAll()
        else chatRewardConfigRepo.findAllByRoomType(roomType))
            .toList()
            .map { it.toChatRewardConfig() }
    }
}