package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.auth.AuthApi
import com.noljanolja.server.consumer.adapter.auth.AuthUser
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.CoreLocalContact.Companion.toCoreLocalContact
import com.noljanolja.server.consumer.adapter.core.CoreUser
import com.noljanolja.server.consumer.adapter.core.CoreUserPreferences
import com.noljanolja.server.consumer.adapter.core.toConsumerUser
import com.noljanolja.server.consumer.model.LocalContact
import com.noljanolja.server.consumer.model.User
import com.noljanolja.server.consumer.rest.request.UpdateCurrentUserRequest
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    suspend fun getFirebaseUser(bearerToken: String): AuthUser? {
        return authApi.getUser(bearerToken)
    }

    suspend fun deleteFirebaseUser(bearerToken: String) {
        authApi.deleteUser(bearerToken)
    }

    suspend fun getCurrentUser(user: AuthUser): User? {
        try {
            val res = coreApi.getUserDetails(user.id)
            return res?.toConsumerUser()
        } catch (e: Exception) {
            val newUser = CoreUser(
                id = user.id,
                name = user.name,
                avatar = user.avatar,
                phone = user.phone,
                email = user.email
            )
            val createdUser = coreApi.upsertUser(newUser)
            return createdUser?.toConsumerUser()
        }
    }

    suspend fun updateCurrentUser(userId: String, request: UpdateCurrentUserRequest) : User? {
        val coreUser = CoreUser(
            id = userId,
            name = request.name,
            email = request.email,
            dob = request.dob,
            gender = request.gender,
            preferences = request.preferences ?: CoreUserPreferences()
        )
        return coreApi.upsertUser(coreUser)?.toConsumerUser()
    }

    suspend fun deleteCurrentUser(userId: String) : Nothing? {
        return coreApi.deleteUser(userId)
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