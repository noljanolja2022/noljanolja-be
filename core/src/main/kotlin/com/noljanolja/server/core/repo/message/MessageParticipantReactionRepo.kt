package com.noljanolja.server.core.repo.message

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageParticipantReactionRepo : CoroutineCrudRepository<MessageParticipantReactionModel, Long> {
    suspend fun findFirstByMessageIdAndParticipantIdAndReactionId(
        messageId: Long,
        participantId: String,
        reactionId: Long,
    ): MessageParticipantReactionModel?

    fun findAllByMessageIdIn(
        messageId: List<Long>,
    ): Flow<MessageParticipantReactionModel>
}