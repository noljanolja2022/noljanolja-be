package com.noljanolja.server.core.repo.message

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageParticipantReactionRepo : CoroutineCrudRepository<MessageParticipantReactionModel, Long> {
    fun findAllByMessageIdIn(
        messageId: List<Long>,
    ): Flow<MessageParticipantReactionModel>

    suspend fun deleteAllByMessageIdAndParticipantId(
        messageId: Long,
        participantId: String,
    )
}