package com.noljanolja.server.core.repo.message

import com.noljanolja.server.core.model.Message
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageStatusRepo : CoroutineCrudRepository<MessageStatusModel, Long> {
    suspend fun existsByMessageIdAndUserIdAndStatus(
        messageId: Long,
        userId: String,
        status: Message.Status
    ): Boolean

    fun findAllByMessageIdInAndStatusOrderByMessageIdDesc(
        messageIds: List<Long>,
        status: Message.Status,
    ): Flow<MessageStatusModel>
}