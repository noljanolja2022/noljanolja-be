package com.noljanolja.server.core.repo.user

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepo : CoroutineCrudRepository<ContactModel, Long> {
    @Query(
        """
        SELECT contacts.* FROM `contacts` INNER JOIN `user_contacts` ON `contacts`.id = `user_contacts`.contact_id
        WHERE `user_contacts`.user_id = :userId 
        AND IF(:isBlocked IS NOT NULL, user_contacts.is_blocked = :isBlocked, TRUE)
        """
    )
    fun findAllContactsOfUser(
        userId: String,
        isBlocked: Boolean? = null,
    ): Flow<ContactModel>

    fun findAllByPhoneNumberIn(
        phones: List<String>,
    ): Flow<ContactModel>

    fun findByPhoneNumberAndCountryCode(phone: String, countryCode: String): Flow<ContactModel>
}