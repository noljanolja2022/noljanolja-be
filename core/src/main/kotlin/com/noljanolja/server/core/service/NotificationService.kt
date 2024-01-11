package com.noljanolja.server.core.service

import com.noljanolja.server.core.repo.notification.NotificationModel
import com.noljanolja.server.core.repo.notification.NotificationRepo
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class NotificationService (
    private val notificationRepo: NotificationRepo
){
    suspend fun createNotification(
        userId: String,
        type: String,
        title: String,
        body: String,
        image: String,
        data: String,
        isRead: Boolean,
    ) {
        notificationRepo.save(
            NotificationModel(
                userId = userId,
                type = type,
                body = body,
                title = title,
                image = image,
                data = data,
                isRead = isRead
            )
        )
    }

    suspend fun getNotifications(userId: String, page: Int, pageSize: Int): List<NotificationModel> {
        return notificationRepo.findAllByUserId(userId, page, pageSize).toList()
    }
}