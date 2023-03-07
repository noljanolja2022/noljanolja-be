package com.noljanolja.server.core.repo.conversation

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface ConversationParticipantRepo : CoroutineCrudRepository<ConversationParticipantModel, Long> {
    fun findAllByParticipantIdAndConversationId(participantId: String, conversationId: Long): Flow<ConversationParticipantModel>
}