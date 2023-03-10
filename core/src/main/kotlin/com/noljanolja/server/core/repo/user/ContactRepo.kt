package com.noljanolja.server.core.repo.user

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepo : CoroutineCrudRepository<ContactModel, Long> {
    @Query(
        """
        SELECT * FROM `contacts`
        INNER JOIN `user_contacts` ON `contacts`.id = `user_contacts`.contact_id AND `user_contacts`.user_id = :userId
        """
    )
    fun findAllByUserId(userId: String): Flow<ContactModel>

    fun findAllByPhoneNumberInOrEmailIn(
        phones: List<String>,
        emails: List<String>,
    ): Flow<ContactModel>
}