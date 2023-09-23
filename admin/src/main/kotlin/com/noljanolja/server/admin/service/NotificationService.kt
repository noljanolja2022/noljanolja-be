package com.noljanolja.server.admin.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationService(
    private val fcm: FirebaseMessaging,
) {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    suspend fun pushToTopic(
        topicName: String,
        pushData: Map<String, String>,
    ) {
        val fcmMessage = Message.builder()
            .putAllData(pushData)
            .setTopic(topicName)
            .build()
        try {
            fcm.send(fcmMessage)
        } catch (error: FirebaseMessagingException) {
            logger.error("Failed to push noti to topic $topicName")
        } catch (error: Exception) {
            logger.error("Notification Exception: ${error.message}")
        }
    }
}