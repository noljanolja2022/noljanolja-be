package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.*
import com.noljanolja.server.consumer.adapter.core.request.*
import com.noljanolja.server.consumer.exception.Error
import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.Message
import com.noljanolja.server.consumer.rest.request.Attachments
import com.noljanolja.server.consumer.rest.request.ConversationUpdateType
import com.noljanolja.server.consumer.rest.request.CoreUpdateAdminOfConversationReq
import com.noljanolja.server.consumer.rest.request.CoreUpdateMemberOfConversationReq
import com.noljanolja.server.consumer.rsocket.SocketRequester
import com.noljanolja.server.consumer.rsocket.UserSendChatMessage
import com.noljanolja.server.consumer.utils.getAttachmentPath
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.apache.tika.Tika
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import com.noljanolja.server.consumer.adapter.core.CoreConversation.Type as CoreConversationType

@Component
class ConversationService(
    private val coreApi: CoreApi,
    private val pubSubService: ConversationPubSubService,
    private val notificationService: NotificationService,
    private val storageService: GoogleStorageService,
    private val socketRequester: SocketRequester,
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
        image: FilePart?,
    ): Conversation {
        image?.headers()?.let {
            when {
                it.contentLength > MAX_IMAGE_SIZE -> throw Error.FileExceedMaxSize
                it.contentType?.toString()?.startsWith(IMAGE_CONTENT_TYPE_PREFIX) != true ->
                    throw Error.InvalidContentType

                else -> null
            }
        }
        var conversation = coreApi.createConversation(
            CreateConversationRequest(
                title = title,
                creatorId = userId,
                type = CoreConversationType.valueOf(type.name),
                participantIds = participantIds,
            )
        )
        image?.let {
            val fileName = "${Instant.now().epochSecond}_${userId}_${it.filename()}"
            val imageDataBuffer = it.content().asFlow()
            val uploadInfo = storageService.uploadFile(
                path = "conversations/${conversation.id}/${fileName}",
                contentType = Tika().detect(imageDataBuffer.first().asInputStream()),
                content = imageDataBuffer.map { it.toByteBuffer() },
                isPublicAccessible = true,
                limitSize = MAX_IMAGE_SIZE
            )
            conversation = coreApi.updateConversation(
                payload = UpdateConversationRequest(
                    imageUrl = uploadInfo.path,
                ),
                conversationId = conversation.id,
            )
        }
        return conversation.toConsumerConversation()
    }

    suspend fun updateConversation(
        userId: String,
        conversationId: Long,
        title: String?,
        image: FilePart?,
        participantIds: Set<String>?,
    ): Conversation {
        val conversation = coreApi.getConversationDetail(
            userId = userId,
            conversationId = conversationId,
        ).toConsumerConversation()
        participantIds?.let {
            if ((conversation.type == Conversation.Type.SINGLE && it != conversation.participants.map { it.id }.toSet())
                || (conversation.type == Conversation.Type.GROUP && it.isEmpty())
            ) throw Error.CannotUpdateConversation
        }
        image?.headers()?.let {
            when {
                it.contentLength > MAX_IMAGE_SIZE -> throw Error.FileExceedMaxSize
                it.contentType?.toString()?.startsWith(IMAGE_CONTENT_TYPE_PREFIX) != true ->
                    throw Error.InvalidContentType

                else -> null
            }
        }
        val newPath = image?.let {
            val fileName = "${Instant.now().epochSecond}_${userId}_${it.filename()}"
            val imageDataBuffer = it.content().asFlow()
            storageService.uploadFile(
                path = "conversations/${conversationId}/${fileName}",
                contentType = Tika().detect(imageDataBuffer.first().asInputStream()),
                content = imageDataBuffer.map { it.toByteBuffer() },
                isPublicAccessible = true,
                limitSize = MAX_IMAGE_SIZE
            ).path
        }
        val res = coreApi.updateConversation(
            payload = UpdateConversationRequest(
                title = title,
                imageUrl = newPath,
                participantIds = participantIds
            ),
            conversationId = conversationId,
        ).toConsumerConversation()
        if (title != null || image != null) {
            withContext(Dispatchers.Default) {
                if (title != null) {
                    launch {
                        createEventMessage(
                            userId,
                            ConversationUpdateType.TITLE.name,
                            Message.Type.EVENT_UPDATED,
                            conversationId,
                        )
                    }
                }
                if (image != null) {
                    launch {
                        createEventMessage(
                            userId,
                            ConversationUpdateType.AVATAR.name,
                            Message.Type.EVENT_UPDATED,
                            conversationId,
                        )
                    }
                }
            }
        }
        return res
    }

    suspend fun createMessage(
        userId: String,
        message: String,
        type: Message.Type,
        conversationId: Long,
        localId: String,
        attachments: Attachments,
        replyToMessageId: Long?,
        shareMessageId: Long?,
    ): Message = coroutineScope {
        validateAttachments(type, attachments)
        var savedMessage = coreApi.saveMessage(
            request = SaveMessageRequest(
                message = message,
                type = CoreMessage.Type.valueOf(type.name),
                senderId = userId,
                replyToMessageId = replyToMessageId,
                shareMessageId = shareMessageId,
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
        if (saveAttachments.isNotEmpty()) {
            savedMessage = coreApi.saveAttachments(
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
        }
        val conversation = coreApi.getConversationDetail(
            userId = userId,
            conversationId = conversationId,
            messageId = savedMessage.id,
        ).toConsumerConversation().apply {
            this.messages.firstOrNull()?.localId = localId
        }
        withContext(Dispatchers.Default) {
            launch {
                socketRequester.emitUserSendChatMessage(
                    UserSendChatMessage(
                        userId = userId,
                        conversationId = conversationId,
                        roomType = conversation.type,
                        creatorId = conversation.creator.id,
                    )
                )
            }
            launch {
                notifyParticipants(conversation)
            }
            launch {
                pushNotifications(conversation)
            }
        }
        savedMessage.apply {
            this.localId = localId
        }
    }

    suspend fun createEventMessage(
        userId: String,
        message: String,
        type: Message.Type,
        conversationId: Long,
    ) {
        val savedMessage = coreApi.saveMessage(
            request = SaveMessageRequest(
                message = message,
                type = CoreMessage.Type.valueOf(type.name),
                senderId = userId,
            ),
            conversationId = conversationId,
        ).toConsumerMessage()
        val conversation = coreApi.getConversationDetail(
            userId = userId,
            conversationId = conversationId,
            messageId = savedMessage.id,
        ).toConsumerConversation().apply {
            this.messages.firstOrNull()?.localId = UUID.randomUUID().toString()
        }
        notifyParticipants(conversation)
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
                messageId = messageId,
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

    suspend fun addMemberToConversation(
        currentUserId: String,
        conversationId: Long,
        newParticipantIds: List<String>,
    ): List<String> {
        val res = coreApi.addMemberToConversation(
            conversationId, CoreUpdateMemberOfConversationReq(
                currentUserId, newParticipantIds
            )
        ) ?: emptyList()
        if (res.isNotEmpty()) {
            withContext(Dispatchers.Default) {
                createEventMessage(
                    currentUserId,
                    res.joinToString(","),
                    Message.Type.EVENT_JOINED,
                    conversationId,
                )
            }
        }
        return newParticipantIds
    }

    suspend fun removeMemberFromConversation(
        currentUserId: String,
        conversationId: Long,
        participantIds: List<String>,
    ): List<String> {
        val res = coreApi.removeMemberFromConversation(
            conversationId, CoreUpdateMemberOfConversationReq(
                currentUserId, participantIds
            )
        ) ?: emptyList()

        withContext(Dispatchers.Default) {
            createEventMessage(
                currentUserId,
                res.joinToString(","),
                Message.Type.EVENT_LEFT,
                conversationId,
            )
        }
        return participantIds
    }

    suspend fun updateAdminOfConversation(
        currentUserId: String,
        conversationId: Long,
        newAdminId: String,
    ): String {
        val res = coreApi.setAdminToConversation(
            conversationId, CoreUpdateAdminOfConversationReq(
                currentUserId, newAdminId
            )
        )
        withContext(Dispatchers.Default) {
            createEventMessage(
                currentUserId,
                ConversationUpdateType.ADMIN.name,
                Message.Type.EVENT_UPDATED,
                conversationId,
            )
        }
        return res ?: newAdminId
    }

    suspend fun reactMessage(
        userId: String,
        reactionId: Long,
        messageId: Long,
        conversationId: Long,
    ) {
        coreApi.reactMessage(
            messageId = messageId,
            reactionId = reactionId,
            participantId = userId,
            conversationId = conversationId,
        )
        CoroutineScope(Dispatchers.IO).launch {
            val conversation = coreApi.getConversationDetail(
                userId = userId,
                conversationId = conversationId,
                messageId = messageId,
            ).toConsumerConversation()
            notifyParticipants(conversation)
        }
    }

    suspend fun clearAllReactions(
        userId: String,
        messageId: Long,
        conversationId: Long,
    ) {
        coreApi.clearAllReactions(
            messageId = messageId,
            conversationId = conversationId,
            participantId = userId,
        )
    }

    suspend fun getAllReactionIcons() = coreApi.getAllReactionIcons().map { it.toMessageReactionIcon() }

    suspend fun removeMessage(
        userId: String,
        conversationId: Long,
        messageId: Long,
        removeForSelfOnly: Boolean,
    ) {
        coreApi.removeMessage(
            removeForSelfOnly = removeForSelfOnly,
            conversationId = conversationId,
            messageId = messageId,
            userId = userId,
        )
        CoroutineScope(Dispatchers.IO).launch {
            val conversation = coreApi.getConversationDetail(
                userId = userId,
                conversationId = conversationId,
                messageId = messageId,
            ).toConsumerConversation()
            notifyParticipants(conversation)
        }
    }

    private suspend fun notifyParticipants(
        conversation: Conversation,
    ) {
        val participantIds = conversation.participants.map { it.id }.toMutableList()
        if (conversation.messages.firstOrNull()?.leftParticipants?.isNotEmpty() == true) {
            participantIds.addAll(conversation.messages.first().leftParticipants.map { it.id })
        }
        withContext(Dispatchers.Default) {
            participantIds.distinct().forEach {
                launch {
                    pubSubService.publish(conversation = conversation, topic = getTopic(it))
                }
            }
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