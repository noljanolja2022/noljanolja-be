package com.noljanolja.server.reward.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserCheckinRecordRepo : CoroutineCrudRepository<UserCheckinRecordModel, Long> {
    suspend fun findFirstByUserIdOrderByCreatedAtDesc(
        userId: String,
    ): UserCheckinRecordModel?


    @Query(
        """
            SELECT * FROM user_checkin_records WHERE user_id = :userId 
            AND MONTH(created_at) = :month AND YEAR(created_at) = :year
        """
    )
    suspend fun findAllCheckinRecordsOfUserInMonthYear(
        userId: String,
        month: Int,
        year: Int,
    ): Flow<UserCheckinRecordModel>
}