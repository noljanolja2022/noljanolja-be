package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.CoreMessage
import com.noljanolja.server.consumer.adapter.core.request.CreateConversationRequest
import com.noljanolja.server.consumer.adapter.core.request.SaveMessageRequest
import com.noljanolja.server.consumer.adapter.core.toConsumerConversation
import com.noljanolja.server.consumer.adapter.core.toConsumerMessage
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.Message
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import com.noljanolja.server.consumer.adapter.core.CoreConversation.Type as CoreConversationType


@Component
class ConversationService(
    private val coreApi: CoreApi,
    private val pubSubService: ConversationPubSubService,
    private val notificationService: NotificationService,
) {
    private fun getTopic(userId: String) = "conversations-$userId"

    suspend fun createConversation(
        title: String,
        participantIds: Set<String>,
        type: Conversation.Type,
    ): Conversation {
        val userId = AuthUserHolder.awaitUser()!!.id
        val conversation = coreApi.createConversation(
            CreateConversationRequest(
                title = title,
                creatorId = userId,
                type = CoreConversationType.valueOf(type.name),
                participantIds = participantIds,
            )
        ).toConsumerConversation()
        coroutineScope {
            notifyParticipants(conversation)
        }
        return conversation
    }

    suspend fun createMessage(
        message: String,
        type: Message.Type,
        conversationId: Long,
    ): Message {
        val userId = AuthUserHolder.awaitUser()!!.id
        val data = coreApi.saveMessage(
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
        ).toConsumerConversation()
        coroutineScope {
            launch {
                notifyParticipants(conversation)
            }
            launch {
                pushNotifications(conversation)
            }
        }
        return data
    }

    suspend fun getUserConversations(): List<Conversation> {
        val userId = AuthUserHolder.awaitUser()!!.id
        return coreApi.getUserConversations(
            userId = userId,
        ).map { it.toConsumerConversation() }
    }

    suspend fun getConversationDetail(
        conversationId: Long,
    ): Conversation {
        val userId = AuthUserHolder.awaitUser()!!.id
        return coreApi.getConversationDetail(
            userId = userId,
            conversationId = conversationId,
        ).toConsumerConversation()
    }

    suspend fun getConversationMessages(
        conversationId: Long,
        beforeMessageId: Long?,
        afterMessageId: Long?,
    ): List<Message> {
        val userId = AuthUserHolder.awaitUser()!!.id
        return coreApi.getConversationMessages(
            userId = userId,
            conversationId = conversationId,
            beforeMessageId = beforeMessageId,
            afterMessageId = afterMessageId,
        ).map { it.toConsumerMessage() }
    }

    private fun notifyParticipants(
        conversation: Conversation
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
}