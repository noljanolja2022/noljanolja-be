package com.noljanolja.server.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Attachment
import com.noljanolja.server.core.model.Conversation
import com.noljanolja.server.core.model.Message
import com.noljanolja.server.core.repo.conversation.*
import com.noljanolja.server.core.repo.message.*
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.core.rest.request.SaveAttachmentsRequest
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ConversationService(
    private val conversationRepo: ConversationRepo,
    private val messageRepo: MessageRepo,
    private val userRepo: UserRepo,
    private val conversationParticipantRepo: ConversationParticipantRepo,
    private val messageStatusRepo: MessageStatusRepo,
    private val attachmentRepo: AttachmentRepo,
    private val objectMapper: ObjectMapper,
) {
    suspend fun createMessage(
        conversationId: Long,
        senderId: String,
        message: String,
        type: Message.Type,
    ): Message {
        conversationParticipantRepo.findAllByParticipantIdAndConversationId(senderId, conversationId).toList().ifEmpty {
            throw Error.UserNotParticipateInConversation
        }
        val sender = userRepo.findById(senderId)!!
        return messageRepo.save(
            MessageModel(
                message = message,
                senderId = senderId,
                conversationId = conversationId,
                type = type,
            )
        ).apply { this.sender = sender }.toMessage(objectMapper)
    }

    suspend fun getConversationMessages(
        conversationId: Long,
        userId: String,
        afterMessageId: Long? = null,
        beforeMessageId: Long? = null,
        limit: Long = 20,
    ): List<Message> {
        conversationParticipantRepo.findAllByParticipantIdAndConversationId(userId, conversationId).toList().ifEmpty {
            throw Error.UserNotParticipateInConversation
        }
        val messages = messageRepo.findAllByConversationId(
            conversationId = conversationId,
            limit = limit,
            beforeMessageId = beforeMessageId,
            afterMessageId = afterMessageId,
        ).toList()
        val attachments = attachmentRepo.findAllByMessageIdIn(messages.map { it.id }.distinct()).toList()
        val uniqueSenderIds = messages.mapTo(mutableSetOf()) { it.senderId }
        val participants = userRepo.findAllById(uniqueSenderIds).toList()
        messages.forEach { message ->
            message.sender = participants.first { it.id == message.senderId }
            message.attachments = attachments.filter { it.messageId == message.id }
        }
        return messages.map { it.toMessage(objectMapper) }
    }

    suspend fun getConversationDetail(
        conversationId: Long,
        userId: String,
        messageLimit: Long = 20,
    ): Conversation {
        conversationParticipantRepo.findAllByParticipantIdAndConversationId(userId, conversationId).toList().ifEmpty {
            throw Error.UserNotParticipateInConversation
        }
        return conversationRepo.findById(conversationId)!!.apply {
            val messages = messageRepo.findAllByConversationId(id, messageLimit).toList()
            val attachments = attachmentRepo.findAllByMessageIdIn(messages.map { it.id }.distinct()).toList()
            val messageStatusSeen = messageStatusRepo.findAllByMessageIdInAndStatusOrderByMessageIdDesc(
                messageIds = messages.map { it.id },
                status = Message.Status.SEEN,
            ).toList().distinctBy { it.userId }
            val uniqueSenderIds = messages.mapTo(mutableSetOf()) { it.senderId }
            val participants = userRepo.findAllById(uniqueSenderIds).toList()
            messages.forEach { message ->
                message.sender = participants.first { it.id == message.senderId }
                message.seenBy = messageStatusSeen.filter { it.messageId == message.id }.map { it.userId }
                message.attachments = attachments.filter { it.messageId == message.id }
            }
            this.messages = messages
            this.participants = userRepo.findAllParticipants(id).toList()
            this.creator = this.participants.first { it.id == creatorId }
        }.toConversation(objectMapper)
    }

    suspend fun getUserConversations(
        userId: String,
        messageLimit: Long = 20,
        senderLimit: Long = 4,
    ): List<Conversation> {
        return userRepo.findById(userId)?.let { user ->
            val conversations = conversationRepo.findAllByUserId(user.id).toList()
            conversations.forEach { conversation ->
                val messages = messageRepo.findAllByConversationId(conversation.id, messageLimit).toList()
                val messageStatusSeen = messageStatusRepo.findAllByMessageIdInAndStatusOrderByMessageIdDesc(
                    messageIds = messages.map { it.id },
                    status = Message.Status.SEEN,
                ).toList().distinctBy { it.userId }
                val attachments = attachmentRepo.findAllByMessageIdIn(messages.map { it.id }.distinct()).toList()
                val uniqueSenderIds = messages.mapTo(mutableSetOf()) { it.senderId }
                val participants = userRepo.findAllById(uniqueSenderIds).toList()
                messages.forEach { message ->
                    message.sender = participants.first { it.id == message.senderId }
                    message.seenBy = messageStatusSeen.filter { it.messageId == message.id }.map { it.userId }
                    message.attachments = attachments.filter { it.messageId == message.id }
                }
                val latestSenders = userRepo.findLatestSender(
                    conversationId = conversation.id,
                    senderLimit = senderLimit,
                ).toList().toMutableList()
                val creator = userRepo.findById(conversation.creatorId)!!
                val allParticipants = userRepo.findAllParticipants(conversation.id).toList()
                val latestParticipants = (latestSenders + creator + allParticipants)
                    .distinctBy { it.id }.take(senderLimit.toInt())
                conversation.messages = messages
                conversation.participants = latestParticipants
                conversation.creator = creator
            }
            conversations.map { it.toConversation(objectMapper) }
        }.orEmpty()
    }

    suspend fun createConversation(
        title: String,
        participantIds: MutableSet<String> = mutableSetOf(),
        type: Conversation.Type,
        creatorId: String,
    ): Conversation {
        if (participantIds.size != userRepo.findAllById(participantIds).toList().size)
            throw Error.ParticipantsNotFound
        val conversation = conversationRepo.save(
            ConversationModel(
                title = title,
                type = type,
                creatorId = creatorId,
            )
        )
        conversationParticipantRepo.saveAll(
            participantIds.map {
                ConversationParticipantModel(
                    participantId = it,
                    conversationId = conversation.id,
                )
            }
        ).toList()
        val participants = userRepo.findAllById(participantIds).toList()
        return conversation.apply {
            this.creator = participants.first { it.id == creatorId }
            this.participants = participants
        }.toConversation(objectMapper)
    }

    suspend fun updateMessageStatus(
        messageId: Long,
        conversationId: Long,
        seenBy: String,
    ) {
        conversationParticipantRepo.findAllByParticipantIdAndConversationId(seenBy, conversationId).toList()
            .ifEmpty { throw Error.UserNotParticipateInConversation }
        val message = messageRepo.findById(messageId)
        if (message?.conversationId != conversationId) throw Error.MessageNotBelongToConversation
        if (!messageStatusRepo.existsByMessageIdAndUserIdAndStatus(
                messageId = messageId,
                userId = seenBy,
                status = Message.Status.SEEN,
            )
        )
            messageStatusRepo.save(
                MessageStatusModel(
                    messageId = messageId,
                    userId = seenBy,
                    status = Message.Status.SEEN,
                )
            )
    }

    suspend fun saveAttachments(
        conversationId: Long,
        messageId: Long,
        attachments: List<SaveAttachmentsRequest.Attachment> = listOf(),
    ): Message {
        val message = messageRepo.findById(messageId)
        if (message?.conversationId != conversationId) throw Error.MessageNotBelongToConversation
        val savedAttachments = attachmentRepo.saveAll(
            attachments.map {
                AttachmentModel(
                    messageId = messageId,
                    name = it.name,
                    originalName = it.originalName,
                    type = it.type,
                    md5 = it.md5,
                    size = it.size,
                )
            }
        ).toList()
        val sender = userRepo.findById(message.senderId)!!
        message.apply {
            this.attachments = savedAttachments
            this.sender = sender
        }
        return message.toMessage(objectMapper)
    }

    suspend fun getAttachmentById(
        userId: String,
        conversationId: Long,
        attachmentId: Long,
    ): Attachment {
        conversationParticipantRepo.findAllByParticipantIdAndConversationId(userId, conversationId).toList().ifEmpty {
            throw Error.UserNotParticipateInConversation
        }
        if (attachmentRepo.countByConversationIdAndAttachmentId(
                attachmentId = attachmentId,
                conversationId = conversationId,
            ) == 0
        ) throw Error.AttachmentNotFound
        return attachmentRepo.findById(attachmentId)!!.toAttachment()
    }
}