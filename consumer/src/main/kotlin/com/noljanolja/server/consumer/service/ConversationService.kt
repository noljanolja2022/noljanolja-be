package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.CoreMessage
import com.noljanolja.server.consumer.adapter.core.CoreConversation.Type as CoreConversationType
import com.noljanolja.server.consumer.adapter.core.request.CreateConversationRequest
import com.noljanolja.server.consumer.adapter.core.request.SaveMessageRequest
import com.noljanolja.server.consumer.adapter.core.toConsumerConversation
import com.noljanolja.server.consumer.adapter.core.toConsumerMessage
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.Message
import org.springframework.stereotype.Component

@Component
class ConversationService(
    private val coreApi: CoreApi,
) {
    suspend fun createConversation(
        title: String,
        participantIds: Set<String>,
        type: Conversation.Type,
    ): Conversation {
        val userId = AuthUserHolder.awaitUser()!!.id
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
        message: String,
        type: Message.Type,
        conversationId: Long,
    ): Message {
        val userId = AuthUserHolder.awaitUser()!!.id
        return coreApi.saveMessage(
            request = SaveMessageRequest(
                message = message,
                type = CoreMessage.Type.valueOf(type.name),
                senderId = userId,
            ),
            conversationId = conversationId,
        ).toConsumerMessage()
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
}