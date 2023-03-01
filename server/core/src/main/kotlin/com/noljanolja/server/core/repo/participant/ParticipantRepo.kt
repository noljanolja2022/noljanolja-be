package com.noljanolja.server.core.repo.participant

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepo : CoroutineCrudRepository<ParticipantModel, Long> {
    suspend fun findAllByConversationIdIn(conversationIds: List<Long>): List<ParticipantModel>
    suspend fun findAllByConversationId(conversationId: Long): List<ParticipantModel>
}