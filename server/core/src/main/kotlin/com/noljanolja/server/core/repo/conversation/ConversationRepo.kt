package com.noljanolja.server.core.repo.conversation

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ConversationRepo : CoroutineCrudRepository<ConversationModel, Long> {
    @Query("SELECT * FROM conversations WHERE id IN (SELECT conversation_id FROM participants WHERE user_id = :userId) ORDER BY updated_at DESC")
    suspend fun findAllByUserId(userId: UUID): List<ConversationModel>
}