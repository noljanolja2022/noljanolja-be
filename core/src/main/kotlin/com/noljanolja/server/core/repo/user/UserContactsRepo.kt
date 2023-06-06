package com.noljanolja.server.core.repo.user

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserContactsRepo : CoroutineCrudRepository<UserContactModel, Long> {
    fun findAllByUserId(userId: String): Flow<UserContactModel>

    fun findByUserIdAndContactId(userId: String, contactId: Long) : Flow<UserContactModel>
}
