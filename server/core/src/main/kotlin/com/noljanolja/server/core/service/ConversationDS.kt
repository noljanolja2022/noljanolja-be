package com.noljanolja.server.core.service

import com.noljanolja.server.core.model.*
import com.noljanolja.server.core.repo.attachment.AttachmentModel
import com.noljanolja.server.core.repo.message.MessageModel
import com.noljanolja.server.core.repo.participant.ParticipantModel
import com.noljanolja.server.core.repo.attachment.AttachmentRepo
import com.noljanolja.server.core.repo.conversation.ConversationModel
import com.noljanolja.server.core.repo.conversation.ConversationRepo
import com.noljanolja.server.core.repo.conversation.toConversation
import com.noljanolja.server.core.repo.message.MessageRepo
import com.noljanolja.server.core.repo.message.toMessage
import com.noljanolja.server.core.repo.participant.ParticipantRepo
import com.noljanolja.server.core.repo.user.UserModel
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.core.repo.user.toUser
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
internal class ConversationDS(
    private val participantRepo: ParticipantRepo,
    private val conversationRepo: ConversationRepo,
    private val attachmentRepo: AttachmentRepo,
    private val messageRepo: MessageRepo,
    private val userRepo: UserRepo
) {
    companion object {
        const val CONVERSATION_LIST_MESSAGE_LIMIT = 20
        const val CONVERSATION_LIST_PARTICIPANT_LIMIT = 4
        const val MAX_IMAGE_SIZE = 10L
        const val MAX_FILE_SIZE = 10L
        const val MAX_VIDEO_SIZE = 100L
        const val IMAGE_CONTENT_TYPE_PREFIX = "image/"
        const val VIDEO_CONTENT_TYPE_PREFIX = "video/"
        val NOT_SUPPORT_ATTACHMENTS_MESSAGE_TYPES = listOf(
            MessageType.PlainText, MessageType.Gif, MessageType.Sticker
        )
    }

    suspend fun findUserConversations(userId: String): List<Conversation> = coroutineScope {
        val conversations = conversationRepo.findAllByUserId(UUID.fromString(userId))
        val conversationIds = conversations.map { it.id }
        val conversationData = awaitAll(async { participantRepo.findAllByConversationIdIn(conversationIds) }, async {
            if (conversationIds.isNotEmpty()) messageRepo.findLatestMessagesOfConversations(conversationIds)
                .fetchMessageAttachments()
            else listOf()
        })
        val participants = conversationData[0] as List<ParticipantModel>
        val latestMessages = conversationData[1] as List<MessageModel>

        val userIds =
            participants.mapTo(mutableSetOf()) { it.userId } + conversations.mapTo(mutableSetOf()) { it.creatorId } + latestMessages.mapTo(
                mutableSetOf()
            ) { it.senderId }
        val userInfos = userRepo.findAllById(userIds).toList()
        val participantGroups = participants.groupBy { it.conversationId }
        conversations.map { conversation ->
            conversation.toConversation(
                participants = participantGroups[conversation.id]!!.mapNotNull { participant ->
                    userInfos.firstOrNull { userInfo ->
                        userInfo.id == participant.userId
                    }
                }.take(CONVERSATION_LIST_PARTICIPANT_LIMIT),
                messages = latestMessages.filter { it.conversationId == conversation.id },
                creator = userInfos.first { it.id == conversation.creatorId },
                senderInfos = userInfos
            )
        }
    }

    suspend fun findConversationById(userId: String, conversationId: Long): Conversation = coroutineScope {
        val conversation =
            conversationRepo.findById(conversationId) ?: throw ConversationError.ConversationNotfound(conversationId)
        val conversationData = awaitAll(async { participantRepo.findAllByConversationId(conversationId) }, async {
            messageRepo.findLatestMessagesOfConversation(
                conversationId = conversationId, limit = CONVERSATION_LIST_MESSAGE_LIMIT
            ).fetchMessageAttachments()
        })
        val participants = conversationData[0] as List<ParticipantModel>
        participants.find { it.userId == UUID.fromString(userId) } ?: throw ConversationError.ConversationNotfound(conversationId)
        val latestMessages = (conversationData[1] as List<MessageModel>).apply { fetchMessageAttachments() }
        val userIds =
            participants.mapTo(mutableSetOf()) { it.userId } + setOf(conversation.creatorId) + latestMessages.mapTo(
                mutableSetOf()
            ) { it.senderId }

        val userInfos = userRepo.findAllById(userIds).toList()
        conversation.toConversation(
            participants = participants.mapNotNull { participant ->
                userInfos.firstOrNull { userInfo ->
                    userInfo.id == participant.userId
                }
            },
            messages = latestMessages,
            creator = userInfos.first { it.id == conversation.creatorId },
            senderInfos = userInfos
        )
    }

    private suspend fun List<MessageModel>.fetchMessageAttachments(): List<MessageModel> {
        val attachmentGroups = takeIf { isNotEmpty() }?.map { it.id }
            ?.let { attachmentRepo.findAllByMessageIdIn(it).groupBy { it.messageId } }.orEmpty()
        return map {
            it.apply {
                attachments = attachmentGroups[it.id].orEmpty()
            }
        }
    }
}