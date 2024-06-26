package com.noljanolja.server.core.repo.conversation

import com.noljanolja.server.core.model.Conversation
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationRepo : CoroutineCrudRepository<ConversationModel, Long> {
    @Query("""
        SELECT conversations.* 
        FROM conversations INNER JOIN conversations_participants 
        ON conversations.id = conversations_participants.conversation_id
        WHERE conversations_participants.participant_id = :userId
        ORDER BY conversations.updated_at DESC
    """)
     fun findAllByUserId(userId: String): Flow<ConversationModel>

     fun findAllByCreatorIdAndType(userId: String, type: Conversation.Type) : Flow<ConversationModel>
     @Query(
         """
             SELECT COUNT(*) FROM conversations
         """
     )
     suspend fun countAll(): Long
     suspend fun countByType(type: Conversation.Type): Long
}