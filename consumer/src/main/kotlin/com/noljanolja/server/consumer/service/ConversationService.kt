package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.*
import com.noljanolja.server.consumer.adapter.core.request.*
import com.noljanolja.server.consumer.adapter.core.request.CreateConversationRequest
import com.noljanolja.server.consumer.exception.Error
import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.Message
import com.noljanolja.server.consumer.model.UploadInfo
import com.noljanolja.server.consumer.rest.request.*
import com.noljanolja.server.consumer.rsocket.SocketRequester
import com.noljanolja.server.consumer.rsocket.UserSendChatMessage
import com.noljanolja.server.consumer.utils.extractLinks
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.apache.tika.Tika
import org.springframework.stereotype.Component
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
        const val AUDIO_CONTENT_TYPE_PREFIX = "audio/"
        const val VIDEO_CONTENT_TYPE_PREFIX = "video/"
        const val MAX_IMAGE_SIZE = 10L * 1024 * 1024
        const val MAX_FILE_SIZE = 10L * 1024 * 1024
    }

    private fun getTopic(userId: String) = "conversations-$userId"

    private suspend fun processFileAndUploadToGCS(
        file: FileAttachment,
        isPublicAccessible: Boolean = false,
    ): UploadInfo {
        val fileName = "${UUID.randomUUID()}_${file.filename}"
        return storageService.uploadFile(
            path = "conversations/${fileName}",
            contentType = Tika().detect(file.data.first().asInputStream()),
            content = file.data.map { it.asByteBuffer() },
            isPublicAccessible = isPublicAccessible,
            fileName = fileName,
        )
    }

    suspend fun createConversation(
        userId: String,
        title: String,
        participantIds: Set<String>,
        type: Conversation.Type,
        image: FileAttachment?,
    ): Conversation {
        image?.let { validateAttachments(Message.Type.PHOTO, listOf(it)) }
        var conversation = coreApi.createConversation(
            CreateConversationRequest(
                title = title,
                creatorId = userId,
                type = CoreConversationType.valueOf(type.name),
                participantIds = participantIds,
            )
        )
        image?.let {
            val uploadInfo = processFileAndUploadToGCS(
                file = it,
                isPublicAccessible = true,
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
        image: FileAttachment?,
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
        val uploadInfo = image?.let {
            validateAttachments(Message.Type.PHOTO, listOf(it))
            processFileAndUploadToGCS(
                file = it,
                isPublicAccessible = true,
            )
        }
        val res = coreApi.updateConversation(
            payload = UpdateConversationRequest(
                title = title,
                imageUrl = uploadInfo?.path,
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
        attachments: List<FileAttachment>,
        replyToMessageId: Long?,
        shareMessageId: Long?,
    ): Message = coroutineScope {
        val savedMessage = createMessageInMultipleConversations(
            userId = userId,
            message = message,
            type = type,
            conversationIds = listOf(conversationId),
            attachments = attachments,
            replyToMessageId = replyToMessageId,
            shareMessageId = shareMessageId,
        ).first().apply {
            this.localId = localId.ifBlank { UUID.randomUUID().toString() }
        }
        val conversation = coreApi.getConversationDetail(
            userId = userId,
            conversationId = conversationId,
            messageLimit = 0,
        ).toConsumerConversation().apply {
            this.messages = listOf(savedMessage)
        }
        withContext(Dispatchers.IO) {
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
        savedMessage
    }

    private suspend fun createMessageInMultipleConversations(
        userId: String,
        message: String,
        type: Message.Type,
        conversationIds: List<Long>,
        attachments: List<FileAttachment>,
        replyToMessageId: Long? = null,
        shareMessageId: Long?,
        shareVideoId: String? = null,
    ): List<Message> = coroutineScope {
        validateAttachments(type, attachments)
        val savedMessages = coreApi.createMessageInMultipleConversations(
            payload = SaveMessageRequest(
                message = message,
                type = CoreMessage.Type.valueOf(type.name),
                senderId = userId,
                replyToMessageId = replyToMessageId,
                shareMessageId = shareMessageId,
                shareVideoId = shareVideoId,
                conversationIds = conversationIds,
            ),
        )
        val uploadedAttachments = attachments.map {
            async {
                val uploadInfo = processFileAndUploadToGCS(it, true)
                Message.Attachment(
                    name = uploadInfo.fileName,
                    originalName = it.filename,
                    type = uploadInfo.contentType,
                    size = uploadInfo.size,
                    md5 = uploadInfo.md5,
                    previewImage = when {
                        uploadInfo.contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX) -> uploadInfo.path
                        uploadInfo.contentType.startsWith(VIDEO_CONTENT_TYPE_PREFIX) -> ""
                        else -> ""
                    },
                    attachmentType = when {
                        uploadInfo.contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX) -> Message.AttachmentType.PHOTO
                        uploadInfo.contentType.startsWith(VIDEO_CONTENT_TYPE_PREFIX) -> Message.AttachmentType.VIDEO
                        else -> Message.AttachmentType.FILE
                    },
                    durationMs = if (uploadInfo.contentType.startsWith(VIDEO_CONTENT_TYPE_PREFIX)) 0 else 0
                )
            }
        }.awaitAll().toMutableList()
        shareVideoId?.let {
            val video = coreApi.getVideoDetails(it).toConsumerVideo()
            uploadedAttachments.add(
                Message.Attachment(
                    name = video.id,
                    originalName = video.title,
                    type = "",
                    size = 0,
                    md5 = "",
                    previewImage = video.thumbnail,
                    attachmentType = Message.AttachmentType.INTERNAL_VIDEO,
                    durationMs = video.durationMs,
                )
            )
        }
        message.extractLinks().forEach {
            uploadedAttachments.add(
                Message.Attachment(
                    name = it,
                    originalName = it,
                    type = "",
                    size = 0,
                    md5 = "",
                    previewImage = "",
                    attachmentType = Message.AttachmentType.LINK,
                )
            )
        }
        savedMessages.map { savedMessage ->
            async {
                if (uploadedAttachments.isNotEmpty()) {
                    coreApi.saveAttachments(
                        payload = SaveAttachmentsRequest(
                            attachments = uploadedAttachments.map {
                                SaveAttachmentsRequest.Attachment(
                                    name = it.name,
                                    type = it.type,
                                    originalName = it.originalName,
                                    size = it.size,
                                    md5 = it.md5,
                                    previewImage = it.previewImage,
                                    attachmentType = it.attachmentType,
                                    durationMs = it.durationMs,
                                )
                            },
                        ),
                        messageId = savedMessage.id,
                        conversationId = savedMessage.conversationId,
                    )
                } else savedMessage
            }
        }.awaitAll().map { it.toConsumerMessage() }
    }

    /**
     * Share a message to multiple conversations
     */
    suspend fun shareMessage(
        userId: String,
        message: String,
        type: Message.Type,
        conversationIds: List<Long>,
        localId: String,
        attachments: List<FileAttachment>,
        shareMessageId: Long?,
        shareVideoId: String?,
    ): List<Message> {
        val savedMessages = createMessageInMultipleConversations(
            userId = userId,
            message = message,
            type = type,
            conversationIds = conversationIds,
            attachments = attachments,
            shareMessageId = shareMessageId,
            shareVideoId = shareVideoId,
        ).onEach { it.localId = UUID.randomUUID().toString() }
        val conversations = coreApi.getConversationDetails(
            userId = userId,
            conversationIds = conversationIds,
            messageLimit = 0,
        ).map { conversation ->
            conversation.messages = savedMessages.filter { it.conversationId == conversation.id }
            conversation.toConsumerConversation()
        }
        withContext(Dispatchers.IO) {
            conversations.forEach { conversation ->
                launch {
                    socketRequester.emitUserSendChatMessage(
                        UserSendChatMessage(
                            userId = userId,
                            conversationId = conversation.id,
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
        }
        return savedMessages
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
        ).toConsumerMessage().apply { this.localId = UUID.randomUUID().toString() }
        val conversation = coreApi.getConversationDetail(
            userId = userId,
            conversationId = conversationId,
            messageLimit = 0,
        ).toConsumerConversation().apply {
            this.messages = listOf(savedMessage)
        }
        notifyParticipants(conversation)
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
        attachmentId: Long,
    ): Message.Attachment {
        return coreApi.getAttachmentById(
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
            if (!removeForSelfOnly) {
                val conversation = coreApi.getConversationDetail(
                    userId = userId,
                    conversationId = conversationId,
                    messageId = messageId,
                ).toConsumerConversation()
                notifyParticipants(conversation)
            }
        }
    }

    suspend fun getConversationAttachments(
        conversationId: Long,
        attachmentTypes: List<Message.AttachmentType>,
        page: Int,
        pageSize: Int,
    ) = coreApi.getConversationAttachments(
        conversationId = conversationId,
        attachmentTypes = attachmentTypes,
        page = page,
        pageSize = pageSize,
    ).let {
        Pair(it.first.map { it.toConsumerAttachment() }, it.second.total)
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
        attachments: List<FileAttachment>,
    ) {
        if (attachments.isNotEmpty()) {
            when (messageType) {
                Message.Type.PHOTO -> attachments.all {
                    when {
                        it.contentType?.startsWith(IMAGE_CONTENT_TYPE_PREFIX) != true -> throw Error.InvalidContentType
                        it.contentLength > MAX_IMAGE_SIZE -> throw Error.FileExceedMaxSize
                        else -> true
                    }
                }

                Message.Type.VOICE -> attachments.all {
                    when {
                        it.contentType?.startsWith(AUDIO_CONTENT_TYPE_PREFIX) != true -> throw Error.InvalidContentType
                        it.contentLength > MAX_FILE_SIZE -> throw Error.FileExceedMaxSize
                        else -> true
                    }
                }

                Message.Type.FILE -> attachments.all {
                    when {
                        it.contentLength > MAX_FILE_SIZE -> throw Error.FileExceedMaxSize
                        else -> true
                    }
                }

                else -> throw Error.InvalidContentType
            }
        }
    }
}