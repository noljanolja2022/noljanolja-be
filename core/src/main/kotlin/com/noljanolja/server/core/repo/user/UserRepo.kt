package com.noljanolja.server.core.repo.user

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : CoroutineCrudRepository<UserModel, String> {
    @Query(
        """
        SELECT * FROM users 
        LIMIT :limit OFFSET :offset
        """
    )
    fun findAll(
        offset: Int,
        limit: Int,
    ): Flow<UserModel>

    @Query(
        """
        SELECT COUNT(id) FROM users 
        WHERE phone_number IN :phones OR email IN :emails
        """
    )
    fun countByPhoneNumberInOrEmailIn(
        phones: List<String>,
        emails: List<String>,
    ): Long

    @Query(
        """
        SELECT * FROM users 
        WHERE phone_number IN :phones OR email IN :emails
        LIMIT :limit OFFSET :offset
        """
    )
    fun findAllByPhoneNumberInOrEmailIn(
        phones: List<String>,
        emails: List<String>,
        offset: Int,
        limit: Int,
    ): Flow<UserModel>

    @Query(
        """
        UPDATE users 
        SET is_deleted = 1 
        WHERE id = :userId
        """
    )
    suspend fun softDeleteById(
        userId: String,
    )
}