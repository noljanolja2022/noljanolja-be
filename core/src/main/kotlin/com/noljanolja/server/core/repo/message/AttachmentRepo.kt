package com.noljanolja.server.core.repo.message

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepo : CoroutineCrudRepository<AttachmentModel, Long> {
    fun findAllByMessageIdIn(
        messageIds: List<Long>,
    ): Flow<AttachmentModel>

    suspend fun findByName(name: String): AttachmentModel?

    @Query(
        """
            SELECT COUNT(*) FROM 
            attachments INNER JOIN messages ON attachments.message_id = messages.id
            AND messages.conversation_id = :conversationId
            AND attachments.id = :attachmentId
        """
    )
    suspend fun countByConversationIdAndAttachmentId(
        conversationId: Long,
        attachmentId: Long,
    ): Int
}