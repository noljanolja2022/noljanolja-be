package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.auth.AuthApi
import com.noljanolja.server.consumer.adapter.auth.AuthUser
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.CoreLocalContact.Companion.toCoreLocalContact
import com.noljanolja.server.consumer.adapter.core.CoreUser
import com.noljanolja.server.consumer.adapter.core.CoreUserPreferences
import com.noljanolja.server.consumer.adapter.core.toConsumerUser
import com.noljanolja.server.consumer.exception.CoreServiceError
import com.noljanolja.server.consumer.model.LocalContact
import com.noljanolja.server.consumer.model.SimpleUser
import com.noljanolja.server.consumer.model.User
import com.noljanolja.server.consumer.rest.request.AddFriendRequest
import com.noljanolja.server.consumer.rest.request.BlockUserRequest
import com.noljanolja.server.consumer.rest.request.UpdateCurrentUserRequest
import com.noljanolja.server.consumer.adapter.core.request.BlockUserRequest as CoreBlockUserRequest
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
        } catch (e: CoreServiceError.UserNotFound) {
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
            avatar = request.avatar,
            gender = request.gender,
            preferences = request.preferences ?: CoreUserPreferences()
        )
        return coreApi.upsertUser(coreUser).toConsumerUser()
    }

    suspend fun deleteCurrentUser(userId: String): Nothing? {
        return coreApi.deleteUser(userId)
    }

    suspend fun getUserContacts(
        userId: String,
        page: Int = 1,
        pageSize: Int = 100
    ): List<SimpleUser> {
        val res = coreApi.getUsers(friendId = userId, page = page, pageSize = pageSize)
        return res?.first ?: emptyList()
    }

    suspend fun syncUserContacts(
        userId: String,
        localContacts: List<LocalContact>,
    ): List<User> {
        val updatedContacts = coreApi.upsertUserContacts(userId, localContacts.map { it.toCoreLocalContact() })
        return updatedContacts.map { it.toConsumerUser() }
    }

    suspend fun findUsers(
        phoneNumber: String? = null
    ): List<SimpleUser> {
        return coreApi.getUsers(phoneNumber = phoneNumber)?.first.orEmpty()
    }

    suspend fun findUserById(
        id: String
    ): SimpleUser {
        val res = coreApi.getUserDetails(id)
        return SimpleUser(
            id = id,
            name = res.name,
            avatar = res.avatar,
            phone = res.phone
        )
    }

    suspend fun sendFriendRequest(
        userId: String,
        req: AddFriendRequest
    ) {
        coreApi.addFriend(userId, req)
    }


    suspend fun getBlackList(
        userId: String,
        page: Int,
        pageSize: Int,
    ) = coreApi.getBlackList(
        userId = userId,
        page = page,
        pageSize = pageSize,
    )

    suspend fun blockUser(
        userId: String,
        request: BlockUserRequest,
    ) {
        coreApi.blockUser(
            userId = userId,
            request = CoreBlockUserRequest(
                blockedUserId = request.blockedUserId,
                isBlocked = request.isBlocked,
            )
        )
    }
}