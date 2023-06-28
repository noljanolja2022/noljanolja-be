package com.noljanolja.server.core.repo.message

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageReactionRepo : CoroutineCrudRepository<MessageReactionModel, Long> {
    fun findAllByOrderByCreatedAtAsc(): Flow<MessageReactionModel>
}