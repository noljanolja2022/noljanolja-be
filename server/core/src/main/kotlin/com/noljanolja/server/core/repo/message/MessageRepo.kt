package com.noljanolja.server.core.repo.message

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepo : CoroutineCrudRepository<MessageModel, Long> {
    @Query(
        "SELECT * FROM messages WHERE id IN (SELECT MAX(id) FROM messages WHERE conversation_id IN (:conversationIds) GROUP BY conversation_id)"
    )
    suspend fun findLatestMessagesOfConversations(conversationIds: List<Long>): List<MessageModel>

    @Query(
        "SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY id DESC LIMIT :limit"
    )
    suspend fun findLatestMessagesOfConversation(conversationId: Long, limit: Int): List<MessageModel>

    @Query(
        "SELECT * FROM messages WHERE  conversation_id = :conversationId AND id > :messageId ORDER BY created_at ASC LIMIT :limit"
    )
    suspend fun findLatestMessageAfter(conversationId: Long, messageId: Long, limit: Int): List<MessageModel>

    @Query(
        "SELECT * FROM messages WHERE  conversation_id = :conversationId AND id < :messageId ORDER BY created_at DESC LIMIT :limit"
    )
    suspend fun findLatestMessageBefore(conversationId: Long, messageId: Long, limit: Int): List<MessageModel>
}