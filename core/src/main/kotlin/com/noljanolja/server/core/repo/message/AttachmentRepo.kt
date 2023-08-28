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

    @Query(
        """
        (SELECT attachments.*, messages.id as message_id_in_conversation FROM attachments INNER JOIN messages ON attachments.message_id = messages.id WHERE messages.conversation_id = :conversationId AND attachment_type IN (:attachmentTypes)
        UNION
        SELECT attachments.*, messages.id as message_id_in_conversation FROM attachments INNER JOIN messages ON attachments.message_id = messages.share_message_id WHERE messages.conversation_id = :conversationId AND attachment_type IN (:attachmentTypes))
        ORDER BY message_id_in_conversation DESC
        LIMIT :limit OFFSET :offset
    """
    )
    fun findAllByConversationIdAndAttachmentTypeIn(
        conversationId: Long,
        attachmentTypes: List<AttachmentType>,
        limit: Int,
        offset: Int,
    ): Flow<AttachmentModel>

    @Query("""
        SELECT COUNT(*) FROM
        (SELECT attachments.*, messages.id as message_id_in_conversation FROM attachments INNER JOIN messages ON attachments.message_id = messages.id WHERE messages.conversation_id = :conversationId AND attachment_type IN (:attachmentTypes)
        UNION
        SELECT attachments.*, messages.id as message_id_in_conversation FROM attachments INNER JOIN messages ON attachments.message_id = messages.share_message_id WHERE messages.conversation_id = :conversationId AND attachment_type IN (:attachmentTypes)) x
    """)
    suspend fun countAllByConversationIdAndAttachmentTypeIn(
        conversationId: Long,
        attachmentTypes: List<AttachmentType>,
    ): Long
}