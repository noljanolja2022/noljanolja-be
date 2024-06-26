package com.noljanolja.server.core.service

import com.noljanolja.server.core.repo.notification.NotificationModel
import com.noljanolja.server.core.repo.notification.NotificationRepo
import com.noljanolja.server.core.exception.Error
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
        val offset = (page - 1) * pageSize
        val limit = pageSize
        return notificationRepo.findAllByUserId(userId, offset, limit).toList()
    }

    suspend fun readNotification(userId: String, id : Long) {
        notificationRepo.updateIsReadByUserIdAndId(userId, id)
    }

    suspend fun readAllNotifications(userId: String) {
       notificationRepo.updateIsReadByUserId(userId)
    }
}