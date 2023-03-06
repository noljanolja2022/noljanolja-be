package com.noljanolja.server.core.repo.user

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : CoroutineCrudRepository<UserModel, String> {
    @Query("SELECT * FROM users LIMIT :limit OFFSET :offset")
    suspend fun findAll(
        offset: Int,
        limit: Int,
    ): List<UserModel>

    @Query("UPDATE users SET is_deleted = 1 WHERE id = :userId")
    suspend fun softDeleteById(
        userId: String,
    )
}