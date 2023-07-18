package com.noljanolja.server.reward.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserCheckinRecordRepo: CoroutineCrudRepository<UserCheckinRecordModel, Long> {
    suspend fun findFirstByUserIdOrderByCreatedAtDesc(
        userId: String,
    ): UserCheckinRecordModel?


    @Query(
        """
            SELECT * FROM user_checkin_rewards WHERE user_id = :userId 
            AND id > (SELECT id FROM user_checkin_rewards WHERE day = 1 and user_id = :userId ORDER BY created_at DESC LIMIT 1)
            ORDER BY created_at ASC
        """
    )
    suspend fun findActiveCheckinRecords(
        userId: String,
    ): Flow<UserCheckinRecordModel>
}