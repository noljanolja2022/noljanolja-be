package com.noljanolja.server.consumer.service

import com.noljanolja.server.common.exception.DefaultNotFoundException
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

    /**
     * @param user Only contain id, need to call Firebase to get full user info
     * @return User
     */
    suspend fun getCurrentUser(user: AuthUser): User {
        return try {
            coreApi.getUserDetails(userId = user.id)
        } catch (e: DefaultNotFoundException) {
            val fullUserData = authApi.getUser(user.bearerToken)
            val newUser = with(fullUserData) {
                CoreUser(
                    id = user.id,
                    name = name,
                    avatar = avatar,
                    phone = phone,
                    email = email
                )
            }
            coreApi.upsertUser(newUser)
        }.toConsumerUser()
    }

    suspend fun updateCurrentUser(userId: String, request: UpdateCurrentUserRequest): User? {
        val coreUser = CoreUser(
            id = userId,
            name = request.name,
            email = request.email,
            dob = request.dob,
            gender = request.gender,
            preferences = request.preferences ?: CoreUserPreferences()
        )
        return coreApi.upsertUser(coreUser).toConsumerUser()
    }

    suspend fun deleteCurrentUser(userId: String): Nothing? {
        return coreApi.deleteUser(userId)
    }

    suspend fun syncUserContacts(
        userId: String,
        localContacts: List<LocalContact>,
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