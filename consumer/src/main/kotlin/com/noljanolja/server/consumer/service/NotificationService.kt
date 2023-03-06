package com.noljanolja.server.consumer.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.Conversation
import org.springframework.stereotype.Component

@Component
class NotificationService(
    private val coreApi: CoreApi,
    private val fcm: FirebaseMessaging,
) {
    suspend fun upsertPushToken(
        userId: String,
        deviceToken: String,
        deviceType: String,
    ) {
        coreApi.upsertPushToken(
            userId = userId,
            deviceType = deviceType,
            deviceToken = deviceToken
        )
    }

    suspend fun pushConversationNotification(
        userId: String,
        conversation: Conversation,
    ) {
        coreApi.getPushToken(userId).forEach { pushToken ->
            val message = conversation.messages.first()
            val pushData = mapOf(
                "conversationId" to conversation.id.toString(),
                "conversationType" to conversation.type.name,
                "senderIcon" to "",
                "senderName" to message.sender.name,
                "message" to message.message,
                "messageId" to message.id.toString(),
                "messageType" to message.type.name,
                "messageTime" to message.createdAt.epochSecond.toString()
            )
            val fcmMessage = Message.builder()
                .putAllData(pushData)
                .setToken(pushToken)
                .build()
            try {
                fcm.send(fcmMessage)
            } catch (error: FirebaseMessagingException) {
                // TODO log
            }
        }
    }
}