package com.noljanolja.server.reward.repo

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRewardRecordRepo : CoroutineCrudRepository<ChatRewardRecordModel, Long> {
    @Query(
        """
            SELECT * FROM chat_reward_records WHERE user_id = :userId AND conversation_id = :conversationId FOR UPDATE
        """
    )
    suspend fun findByUserIdAndConversationIdForUpdate(
        userId: String,
        conversationId: Long,
    ): ChatRewardRecordModel?
}