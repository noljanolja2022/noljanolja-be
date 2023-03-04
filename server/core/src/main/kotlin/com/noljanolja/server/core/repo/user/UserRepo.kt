package com.noljanolja.server.core.repo.user

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepo : CoroutineCrudRepository<UserModel, UUID> {
    @Query("SELECT * FROM users LIMIT :limit OFFSET :offset")
    suspend fun findAllUsers(
        offset: Long,
        limit: Long,
    ): List<UserModel>

    suspend fun findByFirebaseUserId(id: String): UserModel?
}