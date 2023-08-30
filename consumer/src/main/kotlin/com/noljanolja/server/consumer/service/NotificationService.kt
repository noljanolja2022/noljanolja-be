package com.noljanolja.server.consumer.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.Conversation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationService(
    private val coreApi: CoreApi,
    private val fcm: FirebaseMessaging,
) {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)
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
                .setNotification(
                    Notification.builder()
                        .setTitle("New message from ${message.sender.name}")
                        .setBody(message.message)
                        .build()
                )
                .setToken(pushToken)
                .build()
            try {
                fcm.send(fcmMessage)
            } catch (error: FirebaseMessagingException) {

                logger.error("Failed to push noti to user $userId with token $pushToken. Firebase Msg Error: ${error.message}")
            } catch (error: Exception) {
                logger.error("Notification Exception: ${error.message}")
            }
        }
    }
}