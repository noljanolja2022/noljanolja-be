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

    suspend fun countByPhoneNumberInOrEmailIn(
        phones: List<String>,
        emails: List<String>,
    ): Long

    @Query(
        """
        SELECT * FROM users 
        WHERE LOCATE(phone_number, :phones) > 0 OR LOCATE(email, :emails) > 0
        LIMIT :limit OFFSET :offset
        """
    )
    fun findAllByPhoneNumberInOrEmailIn(
        phones: String,
        emails: String,
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

    @Query(
        """
        SELECT users.* FROM 
        (SELECT sender_id, MAX(created_at) AS max_created_at FROM messages WHERE conversation_id = :conversationId 
        GROUP  BY sender_id ORDER BY max_created_at DESC LIMIT :senderLimit) AS v 
        INNER JOIN users ON v.sender_id = users.id ORDER BY v.max_created_at DESC
    """
    )
    fun findLatestSender(
        conversationId: Long,
        senderLimit: Long,
    ): Flow<UserModel>

    @Query("""
        SELECT users.* FROM users INNER JOIN conversations_participants 
        ON users.id = conversations_participants.participant_id
        WHERE conversations_participants.conversation_id = :conversationId
    """)
    fun findAllParticipants(conversationId: Long): Flow<UserModel>
}