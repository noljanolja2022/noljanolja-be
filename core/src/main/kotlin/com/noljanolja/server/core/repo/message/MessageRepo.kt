package com.noljanolja.server.core.repo.message

import com.noljanolja.server.core.model.MessageRange
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepo : CoroutineCrudRepository<MessageModel, Long> {
    @Query(
        """
        SELECT * FROM messages 
        WHERE conversation_id = :conversationId 
        AND IF(:beforeMessageId IS NULL, TRUE, id < :beforeMessageId) 
        AND IF(:afterMessageId IS NULL, TRUE, id > :afterMessageId)
        AND id NOT IN (SELECT message_id FROM message_status WHERE user_id = :userId AND status = 'REMOVED')
        AND sender_id NOT IN (:blackListedUserIds)
        ORDER BY id DESC LIMIT :limit
    """
    )
     fun findAllByConversationId(
        conversationId: Long,
        limit: Long,
        userId: String,
        afterMessageId: Long? = null,
        beforeMessageId: Long? = null,
        blackListedUserIds: List<String> = emptyList(),
    ): Flow<MessageModel>

    @Query(
        """
            SELECT COUNT(*) FROM messages
        """
    )
     suspend fun countAll(): Long

     @Query(
         """
             WITH MessageCounts AS (
                SELECT
                    COUNT(m.id) AS message_count
                FROM
                    messages m
                GROUP BY
                    m.conversation_id
            )
            SELECT
                COALESCE(MIN(message_count),0) AS min_message_count,
                COALESCE(MAX(message_count),0) AS max_message_count
            FROM MessageCounts
         """
     )
     suspend fun findMessageRange(): MessageRange
}