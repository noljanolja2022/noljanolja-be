package com.noljanolja.server.reward.service

import com.noljanolja.server.loyalty.service.LoyaltyService
import com.noljanolja.server.reward.model.ChatRewardConfig
import com.noljanolja.server.reward.repo.*
import com.noljanolja.server.reward.rest.request.UpsertChatConfigRequest
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ChatRewardService(
    private val chatRewardConfigRepo: ChatRewardConfigRepo,
    private val chatRewardRecordRepo: ChatRewardRecordRepo,
    private val loyaltyService: LoyaltyService,
) {
    suspend fun handleRewardUser(
        userId: String,
        conversationId: Long,
        roomType: RoomType,
    ) {
        // Get config for current conversation type
        // if config not exist then return
        val rewardConfig = chatRewardConfigRepo.findByRoomType(roomType)?.takeIf { it.isActive } ?: return
        // get reward record for user in this conversation
        // if record not exist create new
        val rewardRecord = chatRewardRecordRepo.findByUserIdAndConversationIdForUpdate(
            userId = userId,
            conversationId = conversationId,
        ) ?: ChatRewardRecordModel(
            userId = userId,
            conversationId = conversationId,
            applyTimes = 0,
            messageCount = 0,
        )
        // if apply times >= config.maxApplyTimes return
        if (rewardRecord.applyTimes >= rewardConfig.maxApplyTimes) return
        // increase the count by 1
        rewardRecord.messageCount++
        // if meet reward criteria reward the user, reset count and increase apply times by 1
        if (rewardRecord.messageCount >= rewardConfig.numberOfMessages) {
            rewardRecord.messageCount = 0
            rewardRecord.applyTimes++
            loyaltyService.addTransaction(
                memberId = userId,
                point = rewardConfig.rewardPoint,
                reason = "Send messages",
            )
        }
        chatRewardRecordRepo.save(rewardRecord)
    }

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