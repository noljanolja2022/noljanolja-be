package com.noljanolja.server.core.repo.notification

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepo: CoroutineCrudRepository<NotificationModel, Long> {
}