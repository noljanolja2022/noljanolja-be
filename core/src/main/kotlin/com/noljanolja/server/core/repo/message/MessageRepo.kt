package com.noljanolja.server.core.repo.message

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
        ORDER BY id DESC LIMIT :limit
    """
    )
     fun findAllByConversationId(
        conversationId: Long,
        limit: Long,
        afterMessageId: Long? = null,
        beforeMessageId: Long? = null,
    ): Flow<MessageModel>
}