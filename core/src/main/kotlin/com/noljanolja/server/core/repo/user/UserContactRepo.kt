package com.noljanolja.server.core.repo.user

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserContactsRepo : CoroutineCrudRepository<UserContactModel, Long> {
    suspend fun findAllByUserId(userId: Long): List<UserContactModel>
}
