package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.auth.AuthApi
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.CoreLocalContact.Companion.toCoreLocalContact
import com.noljanolja.server.consumer.adapter.core.toConsumerUser
import com.noljanolja.server.consumer.model.LocalContact
import com.noljanolja.server.consumer.model.User
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    suspend fun getCurrentUser(userId: String): User? {
        // TODO set user if not exist
        return coreApi.getUserDetails(userId)?.toConsumerUser()
    }

    suspend fun syncUserContacts(
        userId: String,
        localContacts: List<LocalContact>
    ): List<User> {
        coreApi.upsertUserContacts(userId, localContacts.map { it.toCoreLocalContact() })
        // TODO create new API to get friends with pagination
        val friendsList = coreApi.getUsers(friendId = userId, page = 1, pageSize = 100)
        return when {
            friendsList == null || friendsList.first.isEmpty() -> return emptyList()
            else -> friendsList.first.map { it.toConsumerUser() }
        }
    }
}