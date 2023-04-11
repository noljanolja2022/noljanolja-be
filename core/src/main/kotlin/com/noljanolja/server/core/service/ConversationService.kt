package com.noljanolja.server.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Attachment
import com.noljanolja.server.core.model.Conversation
import com.noljanolja.server.core.model.Message
import com.noljanolja.server.core.repo.conversation.*
import com.noljanolja.server.core.repo.message.*
import com.noljanolja.server.core.repo.user.UserModel
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.core.rest.request.SaveAttachmentsRequest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
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
        // Handle special case so a leaving message can be sent by quitting user
        if (type != Message.Type.EVENT_LEFT)
            conversationParticipantRepo.findAllByParticipantIdAndConversationId(senderId, conversationId).toList()
                .ifEmpty {
                    throw Error.UserNotParticipateInConversation
                }
        val sender = userRepo.findById(senderId)!!
        val savingMessage = MessageModel(
            message = message,
            senderId = senderId,
            conversationId = conversationId,
            type = type,
        )

        var leftJoinParticipants = emptyList<UserModel>()
        if (type == Message.Type.EVENT_LEFT || type == Message.Type.EVENT_JOINED) {
            val leftJoinIds = message.split(",")
            if (leftJoinIds.isEmpty()) {
                throw InvalidParamsException("message")
            }
            if (type == Message.Type.EVENT_LEFT) savingMessage.leftParticipantIds = message
            else savingMessage.joinParticipantIds = message
            savingMessage.message = ""
            leftJoinParticipants = userRepo.findAllById(leftJoinIds).toList()
        }
        return messageRepo.save(
            savingMessage
        ).apply {
            this.sender = sender
            if (type == Message.Type.EVENT_LEFT) this.leftParticipants = leftJoinParticipants
            if (type == Message.Type.EVENT_JOINED) this.joinParticipants = leftJoinParticipants
        }.toMessage(objectMapper)
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
        populateMessages(messages)
        return messages.map { it.toMessage(objectMapper) }
    }

    suspend fun getConversationDetail(
        conversationId: Long,
        userId: String,
        messageLimit: Long = 20,
    ): Conversation {
//        conversationParticipantRepo.findAllByParticipantIdAndConversationId(userId, conversationId).toList().ifEmpty {
//            throw Error.UserNotParticipateInConversation
//        }
        return conversationRepo.findById(conversationId)!!.apply {
            val messages = messageRepo.findAllByConversationId(id, messageLimit).toList()
            populateMessages(messages)
            this.messages = messages
            this.participants = userRepo.findAllParticipants(id).toList()
            this.creator = userRepo.findById(this.creatorId)!!
            getAdminOfConversationModel(this)
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
                populateMessages(messages)
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
                getAdminOfConversationModel(conversation)
            }
            conversations.map { it.toConversation(objectMapper) }
        }.orEmpty()
    }

    suspend fun createConversation(
        title: String,
        participantIds: MutableSet<String> = mutableSetOf(),
        type: Conversation.Type,
        creatorId: String,
        imageUrl: String,
    ): Conversation {
        if (participantIds.size != userRepo.findAllById(participantIds).toList().size)
            throw Error.ParticipantsNotFound
        val conversation = conversationRepo.save(
            ConversationModel(
                title = title,
                type = type,
                creatorId = creatorId,
                adminId = creatorId,
                imageUrl = imageUrl,
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
        return conversation.apply {
            this.creator = userRepo.findById(conversation.creatorId)!!
            getAdminOfConversationModel(this)
            this.participants = userRepo.findAllById(participantIds).toList()
        }.toConversation(objectMapper)
    }

    suspend fun updateConversation(
        id: Long,
        updatedTitle: String?,
        updatedParticipantIds: Set<String>?,
        updatedImageUrl: String?,
    ): Conversation {
        if (updatedParticipantIds != null
            && updatedParticipantIds.size != userRepo.findAllById(updatedParticipantIds).toList().size
        ) throw Error.ParticipantsNotFound
        val conversation = conversationRepo.findById(id) ?: throw Error.ConversationNotFound
        val savedConversation = conversationRepo.save(
            conversation.apply {
                updatedTitle?.takeIf { it.isNotBlank() }?.let {
                    title = it
                }
                updatedImageUrl?.takeIf { it.isNotBlank() }?.let {
                    imageUrl = it
                }
            }
        )
        var currentParticipantIds = conversationParticipantRepo.findAllByConversationId(conversation.id).toList()
            .mapTo(mutableSetOf()) { it.participantId }
        if (updatedParticipantIds != null) {
            val deletedParticipantIds = currentParticipantIds.minus(updatedParticipantIds)
            val newParticipantIds = updatedParticipantIds.minus(currentParticipantIds)
            conversationParticipantRepo.deleteAllByParticipantIdIn(deletedParticipantIds.toList())
            newParticipantIds.map {
                ConversationParticipantModel(
                    participantId = it,
                    conversationId = conversation.id,
                )
            }.also { if (it.isNotEmpty()) conversationParticipantRepo.saveAll(it).toList() }
            currentParticipantIds = updatedParticipantIds.toMutableSet()
        }
        return savedConversation.apply {
            this.creator = userRepo.findById(conversation.creatorId)!!
            getAdminOfConversationModel(this)
            this.participants = userRepo.findAllById(currentParticipantIds).toList()
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

    suspend fun addConversationParticipants(
        conversationId: Long,
        userId: String,
        participants: List<String>
    ): List<String> {
        val conversation = conversationRepo.findById(conversationId)
            ?: throw Error.ConversationNotFound
        if (conversation.adminId != userId) {
            throw Error.NoPermissionToUpdateParticipants
        }
        val currentParticipantIds = conversationParticipantRepo.findAllByConversationId(conversationId).map {
            it.participantId
        }.toSet()
        val newParticipantIds = participants.minus(currentParticipantIds)
        if (newParticipantIds.isNotEmpty()) {
            conversationParticipantRepo.saveAll(
                newParticipantIds.map {
                    ConversationParticipantModel(
                        participantId = it, conversationId = conversationId
                    )
                }
            ).toList()
        }
        return newParticipantIds
    }

    suspend fun removeConversationMember(conversationId: Long, userId: String, participantIds: List<String>) {
        val conversation = conversationRepo.findById(conversationId)
            ?: throw Error.ConversationNotFound
        if (participantIds.size == 1 && participantIds.first() == userId) {
            if (conversation.adminId == userId)
                throw Error.CannotRemoveParticipants
        } else if (conversation.adminId != userId) {
            throw Error.NoPermissionToUpdateParticipants
        }
        conversationParticipantRepo.deleteAllByParticipantIdInAndConversationId(participantIds, conversationId)
    }

    suspend fun assignConversationAdmin(
        conversationId: Long,
        adminId: String,
        assigneeId: String
    ): String {
        val conversation = conversationRepo.findById(conversationId)
            ?: throw Error.ConversationNotFound
        if (conversation.adminId != adminId) {
            throw Error.NoPermissionToUpdateParticipants
        }
        val participants = conversationParticipantRepo.findAllByConversationId(conversationId).toList()
        if (!participants.map { it.participantId }.contains(assigneeId)) {
            throw Error.UserNotParticipateInConversation
        }
        val res = conversationRepo.save(conversation.apply {
            this.adminId = assigneeId
        })
        return res.adminId
    }

    private suspend fun getAdminOfConversationModel(conversation: ConversationModel) {
        conversation.admin = if (conversation.creatorId == conversation.adminId)
            conversation.creator
        else userRepo.findById(conversation.adminId)!!
    }

    private suspend fun populateMessages(
        messages: List<MessageModel>
    ) {
        val uniqueSenderIds = messages.mapTo(mutableSetOf()) { it.senderId }
        val participants = userRepo.findAllById(uniqueSenderIds).toList()
        val attachments = attachmentRepo.findAllByMessageIdIn(messages.map { it.id }.distinct()).toList()
        val messageStatusSeen = messageStatusRepo.findAllByMessageIdInAndStatusOrderByMessageIdDesc(
            messageIds = messages.map { it.id },
            status = Message.Status.SEEN,
        ).toList().distinctBy { it.userId }
        val leftParticipantIds = messages.flatMap {
            it.leftParticipantIds.orEmpty().split(",")
        }.distinct().filter { it.isNotEmpty() }
        val joinParticipantIds = messages.flatMap {
            it.joinParticipantIds.orEmpty().split(",")
        }.distinct().filter { it.isNotEmpty() }
        var leftParticipants = emptyList<UserModel>()
        if (leftParticipantIds.isNotEmpty()) {
            leftParticipants = userRepo.findAllById(leftParticipantIds).toList()
        }
        var joinParticipants = emptyList<UserModel>()
        if (joinParticipantIds.isNotEmpty()) {
            joinParticipants = userRepo.findAllById(joinParticipantIds).toList()
        }
        messages.forEach { message ->
            val leftIds = message.leftParticipantIds?.split(",") ?: emptyList()
            val joinIds = message.joinParticipantIds?.split(",") ?: emptyList()
            if (leftIds.isNotEmpty()) {
                message.leftParticipants = leftParticipants.filter { leftIds.contains(it.id) }
            }
            if (joinIds.isNotEmpty()) {
                message.joinParticipants = joinParticipants.filter { joinIds.contains(it.id) }
            }
            message.sender = participants.first { it.id == message.senderId }
            message.seenBy = messageStatusSeen.filter { it.messageId == message.id }.map { it.userId }
            message.attachments = attachments.filter { it.messageId == message.id }
        }
    }
}