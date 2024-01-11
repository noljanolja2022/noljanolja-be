package com.noljanolja.server.core.repo.notification

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import kotlinx.coroutines.flow.Flow

@Repository
interface NotificationRepo: CoroutineCrudRepository<NotificationModel, Long> {

    @Query(
        """
        SELECT * 
        FROM notifications 
        WHERE
            user_id = :userId
        ORDER BY created_at DESC
        LIMIT :limit  
        OFFSET :offset
    """
    )
    suspend fun findAllByUserId(userId: String, offset: Int, limit: Int): Flow<NotificationModel>
}