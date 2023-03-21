package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.*
import com.noljanolja.server.consumer.adapter.core.request.CreateConversationRequest
import com.noljanolja.server.consumer.adapter.core.request.SaveAttachmentsRequest
import com.noljanolja.server.consumer.adapter.core.request.SaveMessageRequest
import com.noljanolja.server.consumer.adapter.core.request.UpdateMessageStatusRequest
import com.noljanolja.server.consumer.exception.Error
import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.Message
import com.noljanolja.server.consumer.rest.request.Attachments
import com.noljanolja.server.consumer.utils.getAttachmentPath
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.apache.tika.Tika
import org.springframework.stereotype.Component
import java.time.Instant
import com.noljanolja.server.consumer.adapter.core.CoreConversation.Type as CoreConversationType

@Component
class ConversationService(
    private val coreApi: CoreApi,
    private val pubSubService: ConversationPubSubService,
    private val notificationService: NotificationService,
    private val storageService: GoogleStorageService,
) {
    companion object {
        const val IMAGE_CONTENT_TYPE_PREFIX = "image/"
        const val VIDEO_CONTENT_TYPE_PREFIX = "video/"
        const val MAX_IMAGE_SIZE = 10L * 1024 * 1024
    }

    private fun getTopic(userId: String) = "conversations-$userId"

    suspend fun createConversation(
        userId: String,
        title: String,
        participantIds: Set<String>,
        type: Conversation.Type,
    ): Conversation {
        return coreApi.createConversation(
            CreateConversationRequest(
                title = title,
                creatorId = userId,
                type = CoreConversationType.valueOf(type.name),
                participantIds = participantIds,
            )
        ).toConsumerConversation()
    }

    suspend fun createMessage(
        userId: String,
        message: String,
        type: Message.Type,
        conversationId: Long,
        attachments: Attachments,
    ): Message = coroutineScope {
        validateAttachments(type, attachments)
        val savedMessage = coreApi.saveMessage(
            request = SaveMessageRequest(
                message = message,
                type = CoreMessage.Type.valueOf(type.name),
                senderId = userId,
            ),
            conversationId = conversationId,
        ).toConsumerMessage()
        val saveAttachments = attachments.files.map {
            val fileName = "${Instant.now().epochSecond}_${userId}_${it.filename}"
            val contentType = Tika().detect(it.data.first().asInputStream())
            val uploadInfo = storageService.uploadFile(
                path = getAttachmentPath(conversationId, fileName),
                contentType = contentType,
                content = it.data.map { it.asByteBuffer() },
                isPublicAccessible = true,
                limitSize = getLimitSize(
                    messageType = type,
                    contentType = contentType
                )
            )
            Message.Attachment(
                name = fileName,
                originalName = it.filename,
                type = contentType,
                size = uploadInfo.size,
                md5 = uploadInfo.md5,
            )
        }
        val savedMessageWithAttachments = coreApi.saveAttachments(
            payload = SaveAttachmentsRequest(
                attachments = saveAttachments.map {
                    SaveAttachmentsRequest.Attachment(
                        name = it.name,
                        type = it.type,
                        originalName = it.originalName,
                        size = it.size,
                        md5 = it.md5,
                    )
                },
            ),
            conversationId = conversationId,
            messageId = savedMessage.id,
        ).toConsumerMessage()
        val conversation = coreApi.getConversationDetail(
            userId = userId,
            conversationId = conversationId,
        ).toConsumerConversation()
        withContext(Dispatchers.Default) {
            launch {
                notifyParticipants(conversation)
            }
            launch {
                pushNotifications(conversation)
            }
        }
        savedMessageWithAttachments
    }

    fun getLimitSize(
        messageType: Message.Type,
        contentType: String?,
    ): Long {
        return when {
            messageType == Message.Type.PHOTO && contentType?.startsWith(IMAGE_CONTENT_TYPE_PREFIX) ?: false
            -> MAX_IMAGE_SIZE

            else -> 0
        }
    }

    suspend fun getUserConversations(userId: String): List<Conversation> {
        return coreApi.getUserConversations(
            userId = userId,
        ).map { it.toConsumerConversation() }
    }

    suspend fun getConversationDetail(
        userId: String,
        conversationId: Long,
    ): Conversation {
        return coreApi.getConversationDetail(
            userId = userId,
            conversationId = conversationId,
        ).toConsumerConversation()
    }

    suspend fun getConversationMessages(
        userId: String,
        conversationId: Long,
        beforeMessageId: Long?,
        afterMessageId: Long?,
    ): List<Message> {
        return coreApi.getConversationMessages(
            userId = userId,
            conversationId = conversationId,
            beforeMessageId = beforeMessageId,
            afterMessageId = afterMessageId,
        ).map { it.toConsumerMessage() }
    }

    suspend fun seenMessage(
        userId: String,
        messageId: Long,
        conversationId: Long,
    ) {
        try {
            coreApi.updateMessageStatus(
                payload = UpdateMessageStatusRequest(
                    seenBy = userId,
                ),
                messageId = messageId,
                conversationId = conversationId,
            )
            val conversation = coreApi.getConversationDetail(
                userId = userId,
                conversationId = conversationId,
            ).toConsumerConversation()
            notifyParticipants(conversation)
        } catch (e: Exception) {
            println("Failed to update message status: ${e.message}")
        }
    }

    suspend fun getAttachmentById(
        userId: String,
        conversationId: Long,
        attachmentId: Long,
    ): Message.Attachment {
        return coreApi.getAttachmentById(
            userId = userId,
            conversationId = conversationId,
            attachmentId = attachmentId,
        ).toConsumerAttachment()
    }

    private fun notifyParticipants(
        conversation: Conversation,
    ) {
        val participantIds = conversation.participants.map { it.id }
        participantIds.forEach {
            pubSubService.publish(conversation = conversation, topic = getTopic(it))
        }
    }

    private suspend fun pushNotifications(
        conversation: Conversation,
    ) = coroutineScope {
        val senderId = conversation.messages.first().sender.id
        val participantIds = conversation.participants.mapNotNull { user ->
            user.id.takeIf { it != senderId }
        }
        participantIds.map {
            async {
                notificationService.pushConversationNotification(
                    userId = it,
                    conversation = conversation
                )
            }
        }.awaitAll()
    }

    private fun validateAttachments(
        messageType: Message.Type,
        attachments: Attachments,
    ) {
        when {
            !isValidContentType(
                messageType = messageType,
                attachments = attachments,
            ) -> throw Error.InvalidContentType

            attachments.files.any {
                it.contentLength > getLimitSize(
                    messageType = messageType,
                    contentType = it.contentType
                )
            }
            -> throw Error.FileExceedMaxSize
        }
    }

    private fun isValidContentType(
        messageType: Message.Type,
        attachments: Attachments,
    ): Boolean {
        return when (messageType) {
            Message.Type.PHOTO -> attachments.files.all {
                (it.contentType?.startsWith(IMAGE_CONTENT_TYPE_PREFIX) ?: false)
            }

            else -> true
        }
    }
}