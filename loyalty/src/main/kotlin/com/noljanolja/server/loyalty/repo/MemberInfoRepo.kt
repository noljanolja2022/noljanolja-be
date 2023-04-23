package com.noljanolja.server.loyalty.repo

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberInfoRepo: CoroutineCrudRepository<MemberInfoModel, String> {
    @Query(
        """
            SELECT * FROM member_info WHERE id = :memberId FOR UPDATE
        """
    )
    suspend fun findByMemberIdForUpdate(
        memberId: String,
    ): MemberInfoModel?
}