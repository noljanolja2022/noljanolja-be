package com.noljanolja.server.core.service

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepo : CoroutineCrudRepository<User, UUID> {
    @Query("SELECT * FROM users LIMIT :limit OFFSET :offset")
    suspend fun findAllUsers(
        offset: Long,
        limit: Long,
    ): List<User>

    suspend fun findByFirebaseUserId(id: String): User?
}