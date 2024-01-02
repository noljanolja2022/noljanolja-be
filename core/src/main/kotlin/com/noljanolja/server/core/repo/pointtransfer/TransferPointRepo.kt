package com.noljanolja.server.core.repo.pointtransfer

import com.noljanolja.server.core.model.UserTransferPoint
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransferPointRepo : CoroutineCrudRepository<UserTransferPointModel, Long> {
    @Query(
        """
            SELECT * 
            FROM user_transfer_points
            WHERE from_user_id = :fromUserId AND
            to_user_id = :toUserId AND
            type = :type
            ORDER BY created_at DESC
            LIMIT 1
        """
    )
    suspend fun findLatestRequestPoint(
        fromUserId: String,
        toUserId: String,
        type: UserTransferPoint.Type
    ): UserTransferPointModel?
}